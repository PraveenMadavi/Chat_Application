import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import FullScreenLoader from './FullScreenLoader';
import { useNavigate } from 'react-router-dom';


// Replace your axios instance creation with this
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// Add this interceptor to ensure cookies are included
api.interceptors.request.use(config => {
    config.withCredentials = true;
    return config;
}, error => {
    return Promise.reject(error);
});

const AuthForms = () => {
    // Form state
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        username: '',
        password: ''
    });
    // Inside your component
    const navigate = useNavigate();
    const [showSuccess, setShowSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [aesKey, setAesKey] = useState(null); // Store the AES key in state

    const BaseURl = import.meta.env.VITE_API_BASE_URL
    const didRunRef = useRef(false);
    //..........................................................................................
    // 1. Fetch RSA Public Key from Server
    async function getPublicKey() {
        console.log("base url printint.... new chanhes", BaseURl)
        const res = await axios.get(
            `${BaseURl}/crypto/get-public-key`,
            { withCredentials: true } // Send cookies/session info with the request
        );
        const base64Key = res.data;
        // Convert base64 to ArrayBuffer
        const binaryKey = Uint8Array.from(atob(base64Key), c => c.charCodeAt(0));

        // Import the RSA public key into WebCrypto
        return await window.crypto.subtle.importKey(
            "spki",
            binaryKey.buffer,
            { name: "RSA-OAEP", hash: "SHA-1" }, // Match server's RSA config
            true,
            ["encrypt"]
        );
    }

    // 2. Generate AES Key and Send to Server
    async function sendAesKey(publicKey) {
        // Generate AES key
        const aesKey = await crypto.subtle.generateKey(
            { name: "AES-CBC", length: 128 },
            true,
            ["encrypt", "decrypt"]
        );

        // Export raw AES key bytes
        const rawAesKey = await crypto.subtle.exportKey("raw", aesKey);

        // Encrypt AES key with RSA public key
        const encryptedAesKey = await crypto.subtle.encrypt(
            { name: "RSA-OAEP" },
            publicKey,
            rawAesKey
        );

        // Send encrypted AES key to server

        // const response = await axios.post(
        //     `${BaseURl}/crypto/set-aes-key`,
        //     {
        //         encryptedAesKey: btoa(String.fromCharCode(...new Uint8Array(encryptedAesKey)))
        //     },
        //     {
        //         withCredentials: true  // This makes Axios send cookies/session ID
        //     }
        // );

        const response = await api.post('/crypto/set-aes-key', {
            encryptedAesKey: btoa(String.fromCharCode(...new Uint8Array(encryptedAesKey)))
        });




        const sessionId = response.data;

        // Store sessionId in cookie for 7 days
        document.cookie = `token=${sessionId}; path=/; max-age=${7 * 24 * 60 * 60
            }; Secure; SameSite=Strict`;

        console.log("Token ID stored in cookie:", sessionId);


        return aesKey;
    }


    async function sendEncryptedMessage(aesKey, message) {
        // Create a random IV
        const iv = crypto.getRandomValues(new Uint8Array(16));

        // Encode message as bytes
        const encodedMessage = new TextEncoder().encode(message);

        // Encrypt message with AES-CBC
        const encryptedMessage = await crypto.subtle.encrypt(
            { name: "AES-CBC", iv },
            aesKey,
            encodedMessage
        );

        const res = await axios.post(
            `${BaseURl}/crypto/encrypted-message`,
            {
                iv: btoa(String.fromCharCode(...iv)),
                encryptedMessage: btoa(
                    String.fromCharCode(...new Uint8Array(encryptedMessage))
                )
            },
            {
                withCredentials: true // ये जरूरी है same session के लिए
            }
        );

        console.log("Server response:", res.data);

        return res.data
    }

    // useEffect to run on component mount
    useEffect(() => {
        if (didRunRef.current) return; // Skip if already run
        async function run() {
            try {
                const publicKeyBase64 = await getPublicKey();
                const aesKey = await sendAesKey(publicKeyBase64);
                setAesKey(aesKey)
                // const _success = await sendEncryptedMessage(aesKey, "Hello from React client!");
                // console.log("Final", _success)
            } catch (err) {
                console.error("Encryption error:", err);
            }
        }
        if (didRunRef.current) return; // Skip if already run
        didRunRef.current = true;
        run();
    }, []);

    // Add request interceptor to include auth token and encrypt data
    api.interceptors.request.use(async (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        // Encrypt request data if we have an AES key
        if (aesKey && config.data) {
            try {
                const iv = window.crypto.getRandomValues(new Uint8Array(16));
                const encryptedData = await window.crypto.subtle.encrypt(
                    {
                        name: "AES-CBC",
                        iv: iv
                    },
                    aesKey,
                    new TextEncoder().encode(JSON.stringify(config.data))
                );

                config.data = {
                    iv: arrayBufferToBase64(iv),
                    encryptedData: arrayBufferToBase64(encryptedData)
                };
                config.headers['Content-Type'] = 'application/json';
            } catch (error) {
                console.error('Encryption failed:', error);
                throw error;
            }
        }

        return config;
    }, (error) => {
        return Promise.reject(error);
    });

    // Modified response interceptor to handle decryption
    api.interceptors.response.use(
        async (response) => {
            // Decrypt response data if encrypted
            if (aesKey && response.data.encryptedData) {
                try {
                    const iv = base64ToArrayBuffer(response.data.iv);
                    const decryptedData = await window.crypto.subtle.decrypt(
                        {
                            name: "AES-CBC",
                            iv: iv
                        },
                        aesKey,
                        base64ToArrayBuffer(response.data.encryptedData)
                    );

                    response.data = JSON.parse(new TextDecoder().decode(decryptedData));
                } catch (error) {
                    console.error('Decryption failed:', error);
                    throw error;
                }
            }
            return response;
        },
        (error) => {
            if (error.response?.status === 401) {
                localStorage.removeItem('accessToken');
                return Promise.reject(new Error('Authentication failed'));
            }
            return Promise.reject(error);
        }
    );

    // Chat functionality
    const chatContainerRef = useRef(null);
    const [messages, setMessages] = useState([
        { id: 1, isMe: false, message: 'Hey! How are you doing? 👋', time: '2:30 PM' },
        { id: 2, isMe: true, message: 'Hi! I\'m doing great, thanks for asking! 😊', time: '2:32 PM' },
        { id: 3, isMe: false, message: 'That\'s awesome! What are you up to today?', time: '2:35 PM' },
        { id: 4, isMe: true, message: 'Just working on some new projects. How about you?', time: '2:38 PM' },
        { id: 5, isMe: false, message: 'Same here! Been busy with work lately', time: '2:40 PM' },
        { id: 6, isMe: true, message: 'I totally understand. Want to grab coffee later? ☕', time: '2:42 PM' },
    ]);

    // Auto message generation
    useEffect(() => {
        const otherPersonMessages = [
            'Sounds like a great plan! 🎉',
            'What time works best for you?',
            'I just saw your latest post, amazing work! 🚀',
            'Are you free this weekend?',
            'The weather looks perfect today ⛅',
            'I just finished reading that book you recommended 📚',
            'Coffee sounds perfect right now ☕',
            'Looking forward to catching up soon',
            'Did you see the latest updates?',
            'Hope you have a great day! 👏',
        ];

        const myMessages = [
            'Thanks! That means a lot to me 😊',
            'Absolutely! Let\'s make it happen',
            'I\'m free anytime after 3 PM',
            'Perfect! I\'ll see you there',
            'Yes, I saw it! Pretty exciting stuff',
            'You too! Have an amazing day 🌟',
            'I totally agree with you',
            'That\'s exactly what I was thinking',
            'Let me know what you think!',
            'Catch you later! 👋',
        ];

        const interval = setInterval(() => {
            const isMyTurn = Math.random() > 0.6;
            const messageArray = isMyTurn ? myMessages : otherPersonMessages;
            const randomMessage = messageArray[Math.floor(Math.random() * messageArray.length)];
            const currentTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            setMessages(prev => [
                ...prev,
                {
                    id: prev.length + 1,
                    isMe: isMyTurn,
                    message: randomMessage,
                    time: currentTime
                }
            ]);
        }, 4000);

        return () => clearInterval(interval);
    }, []);

    // Auto scroll chat
    useEffect(() => {
        if (chatContainerRef.current) {
            chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
        }
    }, [messages]);

    // Form handlers
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!aesKey) {
            alert('Security initialization not complete');
            return;
        }

        setIsLoading(true);

        try {
            // 1. Create IV
            const iv = crypto.getRandomValues(new Uint8Array(16));

            // 2. Prepare data to be encrypted
            let dataToEncrypt;
            if (isLogin) {
                // For login: Only include username and password
                dataToEncrypt = {
                    username: formData.username,
                    password: formData.password
                };
            } else {
                // For registration: Include all form data
                dataToEncrypt = { ...formData };
            }

            console.log("Data being sent:", dataToEncrypt);


            // // 2. Convert form data to JSON string
            const formDataJson = JSON.stringify(dataToEncrypt);


            // 3. Encrypt the form data
            const encryptedData = await crypto.subtle.encrypt(
                { name: "AES-CBC", iv },
                aesKey,
                new TextEncoder().encode(formDataJson)
            );

            const getCookies = () => {
                try {
                    // Get all cookies as a string
                    const cookies = document.cookie;

                    // Find the specific cookie you want (e.g., 'token')
                    const cookieName = 'token=';
                    const cookieArray = cookies.split(';');

                    for (let i = 0; i < cookieArray.length; i++) {
                        let cookie = cookieArray[i].trim();
                        if (cookie.startsWith(cookieName)) {
                            return cookie.substring(cookieName.length, cookie.length);
                        }
                    }

                    return null; // Return null if cookie not found
                } catch (error) {
                    console.error("Error while getting cookies", error);
                    return null;
                }
            }

            // 4. Prepare payload
            const payload = {
                iv: arrayBufferToBase64(iv),
                encryptedPayload: arrayBufferToBase64(encryptedData),
                token: getCookies() // This will now return the cookie value or null
            };

            // 5. Make the actual request with proper cookie configuration
            const config = {
                withCredentials: true, // This ensures cookies are sent
                headers: {
                    'Content-Type': 'application/json',
                    // Add any additional headers if needed
                    'Accept': 'application/json'
                },
                // Ensure cookies from domain are included
                credentials: 'include'
            };

            console.log("payload", payload)

            const url = `${BaseURl}/auth/${isLogin ? 'login' : 'register'}`;

            const { data } = await axios.post(url, payload, config);

            // 6. Handle success
            if (isLogin) {
                localStorage.setItem('accessToken', data.token);
                // Clear form fields
                setFormData({
                    name: '',
                    email: '',
                    username: '',
                    password: ''
                });

                // Show success modal
                setShowSuccess(true);
                navigate("/AppChatBox"); // Using React Router's useNavigate
                // Redirect after 3 seconds
                setTimeout(() => {
                    setShowSuccess(false);

                }, 1000);
            } else {
                // For registration
                setShowSuccess(true);
                // Hide success modal after 3 seconds
                setTimeout(() => {

                    setShowSuccess(false)
                    setIsLogin(true); // Switch to login form
                }, 1000);
            }


        } catch (error) {
            console.error("Full error details:", {
                message: error.message,
                response: error.response?.data,
                status: error.response?.status,
                config: error.config
            });

            // Custom message for 409 Conflict
            if (error.response?.status === 409) {
                alert("This user already exists. Please try logging in or use a different email.");
            } else {
                alert(error.response?.data?.message || "Request failed");
            }
        } finally {
            setIsLoading(false);
        }
    };

    // Helper function to convert ArrayBuffer to Base64
    function arrayBufferToBase64(buffer) {
        let binary = '';
        const bytes = new Uint8Array(buffer);
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return btoa(binary);
    }
    const handleLogout = async () => {
        try {
            await api.post('/auth/logout');
            localStorage.removeItem('accessToken');
            console.log('Logged out successfully');
            // Redirect or update UI as needed
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    const toggleForm = () => {
        setIsLogin(!isLogin);
    };

    if (isLoading) {
        return (
            <div className="bg-white rounded-lg  p-4 h-Full">
                <FullScreenLoader height="100%" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 relative overflow-hidden">
            {/* Background Effects */}
            <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-purple-900/50 via-slate-900 to-black"></div>

            {/* Animated Particles */}
            <div className="absolute inset-0 pointer-events-none overflow-hidden">
                <div className="absolute w-1 h-1 bg-purple-400/60 rounded-full top-[20%] left-[10%] animate-pulse"></div>
                <div className="absolute w-2 h-2 bg-pink-400/40 rounded-full top-[60%] left-[80%] animate-pulse" style={{ animationDelay: '1s' }}></div>
                <div className="absolute w-1.5 h-1.5 bg-cyan-400/50 rounded-full bottom-[30%] left-[15%] animate-pulse" style={{ animationDelay: '2s' }}></div>
                <div className="absolute w-1 h-1 bg-purple-300/70 rounded-full top-[40%] right-[20%] animate-pulse" style={{ animationDelay: '3s' }}></div>
                <div className="absolute w-32 h-32 bg-gradient-to-br from-purple-500/10 to-pink-500/10 rounded-full top-[10%] left-[5%] animate-float blur-xl"></div>
                <div className="absolute w-24 h-24 bg-gradient-to-br from-cyan-500/10 to-purple-500/10 rounded-full top-[70%] right-[5%] animate-float-delayed blur-xl"></div>
            </div>

            {/* Success Message */}
            {showSuccess && (
                <div className="fixed top-4 right-4 bg-gradient-to-r from-emerald-500 via-green-600 to-emerald-600 text-white px-6 py-3 rounded-xl font-bold shadow-2xl z-50">

                    <h3 className="text-lg font-bold mb-2">
                        {isLogin ? 'Login Successful!' : 'Registration Successful!'}
                    </h3>
                    <p>
                        {isLogin
                            ? 'You will be redirected shortly...'
                            : 'Please login with your credentials'}
                    </p>

                </div>
            )}

            {/* Main Container */}
            <div className="relative z-10 min-h-screen flex flex-col lg:grid lg:grid-cols-2 lg:gap-4  m-8">
                {/* Header - Mobile only (above forms) */}
                <div className="lg:hidden order-1 text-center mb-4">
                    <h2 className="text-2xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 drop-shadow-md animate-pulse">
                        Welcome to Chat Application
                    </h2>
                </div>
                {/* Auth Forms Section - On top for mobile */}
                <div className="order-1 lg:order-2 flex items-center justify-center mb-6 lg:mb-0">
                    <div className="w-full max-w-md bg-white/5 backdrop-blur-2xl rounded-2xl border border-white/10 p-6 shadow-2xl">
                        <h2 className="text-white text-2xl font-light mb-4 text-center">
                            {isLogin ? 'Welcome Back' : 'Create Account'}
                        </h2>

                        <form onSubmit={handleSubmit}>
                            {!isLogin && (
                                <>
                                    <div className="mb-4">
                                        <input
                                            type="text"
                                            name="name"
                                            placeholder="Full Name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            className="w-full p-3 bg-white/10 rounded-xl border border-white/20 text-white placeholder-white/50 focus:outline-none"
                                            required
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <input
                                            type="email"
                                            name="email"
                                            placeholder="Email Address"
                                            value={formData.email}
                                            onChange={handleChange}
                                            className="w-full p-3 bg-white/10 rounded-xl border border-white/20 text-white placeholder-white/50 focus:outline-none"
                                            required
                                        />
                                    </div>
                                </>
                            )}

                            <div className="mb-4">
                                <input
                                    type="text"
                                    name="username"
                                    placeholder="Username"
                                    value={formData.username}
                                    onChange={handleChange}
                                    className="w-full p-3 bg-white/10 rounded-xl border border-white/20 text-white placeholder-white/50 focus:outline-none"
                                    required
                                />
                            </div>

                            <div className="mb-6">
                                <input
                                    type="password"
                                    name="password"
                                    placeholder="Password"
                                    value={formData.password}
                                    onChange={handleChange}
                                    className="w-full p-3 bg-white/10 rounded-xl border border-white/20 text-white placeholder-white/50 focus:outline-none"
                                    required
                                />
                            </div>

                            <button
                                type="submit"
                                disabled={isLoading}
                                className="w-full p-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold rounded-xl disabled:opacity-70"
                            >
                                {isLoading ? 'Processing...' : (isLogin ? 'Sign In' : 'Sign Up')}
                            </button>
                        </form>

                        <p className="text-center mt-4 text-white/70 text-xs">
                            {isLogin ? "Don't have an account? " : "Already have an account? "}
                            <button onClick={toggleForm} className="text-purple-300 font-semibold hover:text-white">
                                {isLogin ? 'Sign Up' : 'Sign In'}
                            </button>
                        </p>
                    </div>
                </div>

                {/* Chat Section - Below for mobile */}
                <div className="order-3 lg:order-1 flex flex-col flex-1">
                    {/* Header - Desktop only (inside chat section) */}
                    <div className="hidden lg:block text-center lg:text-left mb-4">
                        <h2 className="text-2xl md:text-5xl mt-[-30px] font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 drop-shadow-md animate-pulse text-center">
                            Welcome to Chat Application
                        </h2>
                    </div>

                    <div className="rounded-2xl border border-white/10 flex flex-col relative overflow-hidden h-[400px] lg:flex-1 lg:max-h-[400px] lg:max-w-[450px] lg:ml-[160px]">
                        <div className="absolute inset-0 bg-gradient-to-br from-white/5 via-transparent to-purple/5"></div>

                        {/* Chat Header */}
                        <div className="relative z-10 p-4 border-b border-white/10">
                            <div className="flex items-center space-x-3">
                                <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-semibold text-sm">R</span>
                                </div>
                                <div className="flex-1">
                                    <h3 className="text-white text-lg font-semibold">Rushikesh Alone</h3>
                                    <div className="flex items-center space-x-2">
                                        <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                                        <span className="text-white/60 text-xs">Online</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Chat Messages with hidden scrollbar */}
                        <div
                            ref={chatContainerRef}
                            className="flex-1 p-4 overflow-y-auto space-y-3 scrollbar-hide"
                        >
                            {messages.map((msg) => (
                                <div key={msg.id} className={`flex ${msg.isMe ? 'justify-end' : 'justify-start'}`}>
                                    <div className={`max-w-[70%] relative ${msg.isMe ? 'bg-gradient-to-r from-purple-600 to-purple-500' : 'bg-white/10'} rounded-2xl p-3 border ${msg.isMe ? 'border-purple-400/30' : 'border-white/10'}`}>
                                        <p className={`${msg.isMe ? 'text-white' : 'text-white/90'} text-sm mb-1`}>
                                            {msg.message}
                                        </p>
                                        <div className="flex items-center justify-end space-x-1">
                                            <span className={`${msg.isMe ? 'text-purple-200' : 'text-white/40'} text-xs`}>
                                                {msg.time}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Chat Input */}
                        <div className="p-4 border-t border-white/10 relative z-10">
                            <div className="flex items-center space-x-3">
                                <input
                                    type="text"
                                    placeholder="Type a message..."
                                    className="w-full p-3 bg-white/10 backdrop-blur-sm border border-white/10 rounded-xl text-white placeholder-white/50 focus:outline-none"
                                />
                                <button className="p-3 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl">
                                    <span className="text-white">→</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <style jsx global>{`
    .scrollbar-hide::-webkit-scrollbar {
        display: none;
        width: 0;
        height: 0;
    }
    .scrollbar-hide {
        -ms-overflow-style: none;
        scrollbar-width: none;
    }
    @keyframes float {
        0%, 100% { transform: translateY(0px); opacity: 0.5; }
        50% { transform: translateY(-20px); opacity: 0.8; }
    }
    @keyframes float-delayed {
        0%, 100% { transform: translateY(0px); opacity: 0.4; }
        50% { transform: translateY(-15px); opacity: 0.7; }
    }
    .animate-float { animation: float 8s ease-in-out infinite; }
    .animate-float-delayed { animation: float-delayed 10s ease-in-out infinite; animation-delay: 2s; }
    .animate-pulse { animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite; }
`}</style>

        </div>
    );
};

export default AuthForms;