import React, { useState, useEffect, useRef } from 'react';
import { Search, Phone, Video, MoreVertical, Smile, Paperclip, Send, Check, CheckCheck, Settings, Plus, ArrowLeft, LogOut, User } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const AppChatBox = () => {
    const [selectedChat, setSelectedChat] = useState(0);
    const [message, setMessage] = useState('');
    const [searchQuery, setSearchQuery] = useState('');
    const [chatData, setChatData] = useState([]);
    const [showMobileChatList, setShowMobileChatList] = useState(true);
    const [showSettingsMenu, setShowSettingsMenu] = useState(false);
    const messagesEndRef = useRef(null);
    const messagesContainerRef = useRef(null);
    const settingsMenuRef = useRef(null);
    const navigate = useNavigate();

    // Auto scroll to bottom
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    // Close settings menu when clicking outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (settingsMenuRef.current && !settingsMenuRef.current.contains(event.target)) {
                setShowSettingsMenu(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    useEffect(() => {
        scrollToBottom();
    }, [chatData, selectedChat]);

    // Initialize chat data
    useEffect(() => {
        setChatData([
            {
                id: 1,
                name: "Rushikesh Alone",
                avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=80&h=80&fit=crop&crop=face",
                lastMessage: "Hey! How are you doing today? 😊",
                time: "10:30 AM",
                unread: 2,
                online: true,
                typing: false,
                lastSeen: "10:30 AM",
                messages: [
                    { id: 1, text: "Hello! How are you?", time: "10:25 AM", sender: "other", status: "read" },
                    { id: 2, text: "I'm doing great! Thanks for asking 😊", time: "10:27 AM", sender: "me", status: "read" },
                    { id: 3, text: "Hey! How are you doing today? 😊", time: "10:30 AM", sender: "other", status: "unread" },
                    { id: 4, text: "What are your plans for the weekend?", time: "10:31 AM", sender: "other", status: "unread" },
                ]
            },
            {
                id: 2,
                name: "Gaurav Tembhare",
                avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&crop=face",
                lastMessage: "Can we meet tomorrow for coffee?",
                time: "9:45 AM",
                unread: 0,
                online: true,
                typing: false,
                lastSeen: "9:46 AM",
                messages: [
                    { id: 1, text: "Hi Mike! Hope you're having a great week", time: "9:30 AM", sender: "me", status: "read" },
                    { id: 2, text: "Hey! Thanks, it's been pretty good!", time: "9:40 AM", sender: "other", status: "read" },
                    { id: 3, text: "Can we meet tomorrow for coffee?", time: "9:45 AM", sender: "other", status: "read" },
                    { id: 4, text: "Absolutely! I know a great place ☕", time: "9:46 AM", sender: "me", status: "delivered" },
                ]
            },
            {
                id: 3,
                name: "Emma Wilson",
                avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&crop=face",
                lastMessage: "Thanks for all your help! 🙏",
                time: "Yesterday",
                unread: 0,
                online: false,
                typing: false,
                lastSeen: "Yesterday 5:22 PM",
                messages: [
                    { id: 1, text: "Could you help me with the project?", time: "Yesterday 4:30 PM", sender: "other", status: "read" },
                    { id: 2, text: "Of course! What do you need help with?", time: "Yesterday 4:35 PM", sender: "me", status: "read" },
                    { id: 3, text: "Thanks for all your help! 🙏", time: "Yesterday 5:20 PM", sender: "other", status: "read" },
                    { id: 4, text: "Happy to help anytime! 😊", time: "Yesterday 5:22 PM", sender: "me", status: "read" },
                ]
            },
            {
                id: 4,
                name: "Development Team b",
                avatar: "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=80&h=80&fit=crop&crop=face",
                lastMessage: "Alex: New feature is ready! 🚀",
                time: "Yesterday",
                unread: 5,
                online: false,
                isGroup: true,
                typing: false,
                participants: [
                    { name: "Alex", avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face", online: true },
                    { name: "Jordan", avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=40&h=40&fit=crop&crop=face", online: true },
                    { name: "Sam", avatar: "https://images.unsplash.com/photo-1519244703995-f4e0f30006d5?w=40&h=40&fit=crop&crop=face", online: false },
                    { name: "Taylor", avatar: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=40&h=40&fit=crop&crop=face", online: true }
                ],
                messages: [
                    { id: 1, text: "Hey everyone! How's the new sprint going?", time: "Yesterday 2:00 PM", sender: "other", senderName: "Alex", senderAvatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face", status: "read" },
                    { id: 2, text: "Making great progress! The API integration is almost done 🔥", time: "Yesterday 2:15 PM", sender: "me", status: "read" },
                    { id: 3, text: "Awesome work! The frontend is looking amazing too", time: "Yesterday 2:20 PM", sender: "other", senderName: "Jordan", senderAvatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=40&h=40&fit=crop&crop=face", status: "read" },
                    { id: 4, text: "New feature is ready! 🚀", time: "Yesterday 2:30 PM", sender: "other", senderName: "Alex", senderAvatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face", status: "unread" },
                    { id: 5, text: "Can't wait to test it out!", time: "Yesterday 2:35 PM", sender: "other", senderName: "Sam", senderAvatar: "https://images.unsplash.com/photo-1519244703995-f4e0f30006d5?w=40&h=40&fit=crop&crop=face", status: "unread" },
                    { id: 6, text: "Great job team! 🎉", time: "Yesterday 2:40 PM", sender: "other", senderName: "Taylor", senderAvatar: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=40&h=40&fit=crop&crop=face", status: "unread" },
                    { id: 7, text: "When do we deploy to production?", time: "Yesterday 2:45 PM", sender: "other", senderName: "Jordan", senderAvatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=40&h=40&fit=crop&crop=face", status: "unread" },
                    { id: 8, text: "Let's schedule a quick review meeting", time: "Yesterday 2:50 PM", sender: "other", senderName: "Alex", senderAvatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face", status: "unread" },
                ]
            },
            {
                id: 5,
                name: "David Park",
                avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&h=80&fit=crop&crop=face",
                lastMessage: "The presentation went really well! 👍",
                time: "2 days ago",
                unread: 1,
                online: false,
                typing: false,
                lastSeen: "2 days ago 4:30 PM",
                messages: [
                    { id: 1, text: "How did your presentation go today?", time: "2 days ago 3:00 PM", sender: "me", status: "read" },
                    { id: 2, text: "It was amazing! Thanks for all your help preparing", time: "2 days ago 3:15 PM", sender: "other", status: "read" },
                    { id: 3, text: "The presentation went really well! 👍", time: "2 days ago 4:30 PM", sender: "other", status: "unread" },
                ]
            }
        ]);
    }, []);

    // Filter chats based on search query
    const filteredChats = chatData.filter(chat =>
        chat.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        chat.lastMessage.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const currentChat = filteredChats[selectedChat] || chatData[0];

    // Mark messages as read when chat is opened
    const markMessagesAsRead = (chatId) => {
        setChatData(prevData =>
            prevData.map(chat =>
                chat.id === chatId
                    ? {
                        ...chat,
                        unread: 0,
                        messages: chat.messages.map(msg =>
                            msg.sender === 'other' ? { ...msg, status: 'read' } : msg
                        )
                    }
                    : chat
            )
        );
    };

    const sendMessage = () => {
        if (message.trim() && currentChat) {
            const newMessage = {
                id: Date.now(),
                text: message.trim(),
                time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                sender: 'me',
                status: 'sent'
            };

            setChatData(prevData =>
                prevData.map(chat =>
                    chat.id === currentChat.id
                        ? {
                            ...chat,
                            messages: [...chat.messages, newMessage],
                            lastMessage: message.trim(),
                            time: 'now'
                        }
                        : chat
                )
            );

            setMessage('');

            // Simulate message delivery
            setTimeout(() => {
                setChatData(prevData =>
                    prevData.map(chat =>
                        chat.id === currentChat.id
                            ? {
                                ...chat,
                                messages: chat.messages.map(msg =>
                                    msg.id === newMessage.id ? { ...msg, status: 'delivered' } : msg
                                )
                            }
                            : chat
                    )
                );
            }, 1000);

            // Simulate message read
            setTimeout(() => {
                setChatData(prevData =>
                    prevData.map(chat =>
                        chat.id === currentChat.id
                            ? {
                                ...chat,
                                messages: chat.messages.map(msg =>
                                    msg.id === newMessage.id ? { ...msg, status: 'read' } : msg
                                )
                            }
                            : chat
                    )
                );
            }, 3000);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    const handleChatSelect = (index) => {
        setSelectedChat(index);
        const selectedChatData = filteredChats[index];
        if (selectedChatData && selectedChatData.unread > 0) {
            markMessagesAsRead(selectedChatData.id);
        }
        // On mobile, hide chat list after selecting a chat
        if (window.innerWidth < 768) {
            setShowMobileChatList(false);
        }
    };

    const handleLogout = () => {
        // In a real app, you would handle the logout logic here
        console.log("User logged out");
        localStorage.removeItem('userToken'); // Clear user token
        navigate('/'); // Redirect to login page
    };

    const toggleMobileChatList = () => {
        setShowMobileChatList(!showMobileChatList);
    };

    return (
        <div className="flex h-screen bg-gradient-to-br from-indigo-50 via-purple-50 to-pink-50 overflow-hidden">
            {/* Sidebar - Hidden on mobile when chat is open */}
            <div className={`w-96 bg-white/80 backdrop-blur-xl border-r border-white/20 flex flex-col shadow-2xl transition-all duration-300 md:relative absolute inset-0 z-10 ${showMobileChatList ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}`}>
                {/* Header */}
                <div className="bg-gradient-to-r from-purple-600 to-indigo-600 p-6 text-white">
                    <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-6 bg-white/20 rounded-full flex items-center justify-center backdrop-blur-sm">
                                <div className="w-6 h-6 bg-white rounded-full"></div>
                            </div>
                            <h1 className="text-xl font-bold">Messages</h1>
                        </div>
                        <div className="flex space-x-2">
                            <button className="p-2 hover:bg-white/20 rounded-xl transition-all duration-200">
                                <Plus size={20} />
                            </button>
                            <div className="relative" ref={settingsMenuRef}>
                                <button
                                    className="p-2 hover:bg-white/20 rounded-xl transition-all duration-200"
                                    onClick={() => setShowSettingsMenu(!showSettingsMenu)}
                                >
                                    <Settings size={20} />
                                </button>
                                {showSettingsMenu && (
                                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-20">
                                        <button
                                            className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-purple-50 w-full text-left"
                                            onClick={() => {
                                                setShowSettingsMenu(false);
                                                // Profile action would go here
                                            }}
                                        >
                                            <User size={16} className="mr-2" />
                                            Profile
                                        </button>
                                        <button
                                            className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-purple-50 w-full text-left"
                                            onClick={() => {
                                                setShowSettingsMenu(false);
                                                handleLogout();
                                            }}
                                        >
                                            <LogOut size={16} className="mr-2" />
                                            Logout
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                    <div className="relative">
                        <Search size={16} className="absolute left-4 top-3.5 text-white/70" />
                        <input
                            type="text"
                            placeholder="Search conversations..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full pl-12 pr-4 py-3 bg-white/20 backdrop-blur-sm rounded-xl text-white placeholder-white/70 focus:outline-none focus:bg-white/30 focus:ring-2 focus:ring-white/50 transition-all duration-200"
                        />
                    </div>
                </div>

                {/* Chat List */}
                <div className="flex-1 overflow-y-auto">
                    {filteredChats.length === 0 ? (
                        <div className="p-8 text-center">
                            <div className="text-gray-400 mb-2">
                                <Search size={48} className="mx-auto mb-4 opacity-50" />
                            </div>
                            <p className="text-gray-500">No conversations found</p>
                        </div>
                    ) : (
                        filteredChats.map((chat, index) => (
                            <div
                                key={chat.id}
                                onClick={() => handleChatSelect(index)}
                                className={`p-4 border-b border-gray-50 cursor-pointer hover:bg-gradient-to-r hover:from-purple-50 hover:to-indigo-50 transition-all duration-300 ${selectedChat === index ? 'bg-gradient-to-r from-purple-100 to-indigo-100 border-r-4 border-purple-500' : ''
                                    }`}
                            >
                                <div className="flex items-center space-x-4">
                                    <div className="relative">
                                        {chat.isGroup ? (
                                            <div className="relative">
                                                <img
                                                    src={chat.avatar}
                                                    alt={chat.name}
                                                    className="w-14 h-14 rounded-full object-cover shadow-lg ring-2 ring-white"
                                                />
                                                <div className="absolute -bottom-1 -right-1 bg-purple-500 text-white text-xs rounded-full px-1.5 py-0.5 font-bold shadow-lg">
                                                    {chat.participants?.length || 0}
                                                </div>
                                            </div>
                                        ) : (
                                            <>
                                                <img
                                                    src={chat.avatar}
                                                    alt={chat.name}
                                                    className="w-14 h-14 rounded-full object-cover shadow-lg ring-2 ring-white"
                                                />
                                                {chat.online && (
                                                    <div className="absolute bottom-1 right-1 w-4 h-4 bg-green-500 rounded-full border-2 border-white shadow-sm"></div>
                                                )}
                                            </>
                                        )}
                                        {chat.unread > 0 && (
                                            <div className="absolute -top-1 -right-1 w-6 h-4 bg-gradient-to-r from-red-500 to-pink-500 rounded-full border-2 border-white flex items-center justify-center shadow-lg">
                                                <span className="text-white text-xs font-bold">{chat.unread}</span>
                                            </div>
                                        )}
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <div className="flex justify-between items-start">
                                            <h3 className={`font-semibold truncate text-base ${chat.unread > 0 ? 'text-gray-900' : 'text-gray-700'}`}>
                                                {chat.name}
                                            </h3>
                                            <span className="text-xs text-gray-500 font-medium">{chat.time}</span>
                                        </div>
                                        <div className="mt-1">
                                            {chat.typing ? (
                                                <div className="flex items-center space-x-1">
                                                    <div className="flex space-x-1">
                                                        <div className="w-1.5 h-1.5 bg-purple-500 rounded-full animate-bounce"></div>
                                                        <div className="w-1.5 h-1.5 bg-purple-500 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                                                        <div className="w-1.5 h-1.5 bg-purple-500 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                                                    </div>
                                                    <span className="text-sm text-purple-600 font-medium">typing...</span>
                                                </div>
                                            ) : (
                                                <p className={`text-sm truncate ${chat.unread > 0 ? 'text-gray-800 font-medium' : 'text-gray-600'}`}>
                                                    {chat.lastMessage}
                                                </p>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            {/* Main Chat Area */}
            <div className={`flex-1 flex flex-col bg-white/50 backdrop-blur-sm ${!showMobileChatList ? 'block' : 'hidden md:flex'}`}>
                {/* Chat Header */}
                <div className="bg-white/80 backdrop-blur-xl p-2 border-b border-white/20 shadow-sm">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                            {/* Back button for mobile */}
                            <button
                                className="md:hidden p-2 hover:bg-purple-100 rounded-xl transition-all duration-200"
                                onClick={toggleMobileChatList}
                            >
                                <ArrowLeft size={20} className="text-gray-600" />
                            </button>

                            <div className="relative">
                                {currentChat?.isGroup ? (
                                    <div className="flex items-center space-x-2">
                                        <img
                                            src={currentChat.avatar}
                                            alt={currentChat.name}
                                            className="w-12 h-12 rounded-full object-cover shadow-lg ring-2 ring-white"
                                        />
                                        <div className="flex -space-x-2">
                                            {currentChat.participants?.slice(0, 3).map((participant, idx) => (
                                                <img
                                                    key={idx}
                                                    src={participant.avatar}
                                                    alt={participant.name}
                                                    className="w-8 h-8 rounded-full border-2 border-white shadow-sm"
                                                />
                                            ))}
                                        </div>
                                    </div>
                                ) : (
                                    <>
                                        <img
                                            src={currentChat?.avatar}
                                            alt={currentChat?.name}
                                            className="w-12 h-12 rounded-full object-cover shadow-lg ring-2 ring-white"
                                        />
                                        {currentChat?.online && (
                                            <div className="absolute bottom-0 right-0 w-3.5 h-3.5 bg-green-500 rounded-full border-2 border-white"></div>
                                        )}
                                    </>
                                )}
                            </div>
                            <div>
                                <h2 className="font-semibold text-gray-900 text-lg">{currentChat?.name}</h2>
                                <p className="text-sm text-gray-500">
                                    {currentChat?.isGroup ? (
                                        <span>{currentChat.participants?.length} members</span>
                                    ) : currentChat?.typing ? (
                                        <span className="text-purple-600 font-medium">typing...</span>
                                    ) : currentChat?.online ? (
                                        'Online now'
                                    ) : (
                                        `Last seen ${currentChat?.lastSeen}`
                                    )}
                                </p>
                            </div>
                        </div>
                        <div className="flex space-x-2">
                            <button className="p-3 hover:bg-purple-100 rounded-xl transition-all duration-200 group">
                                <Phone size={20} className="text-gray-600 group-hover:text-purple-600" />
                            </button>
                            <button className="p-3 hover:bg-purple-100 rounded-xl transition-all duration-200 group">
                                <Video size={20} className="text-gray-600 group-hover:text-purple-600" />
                            </button>
                            <button className="p-3 hover:bg-purple-100 rounded-xl transition-all duration-200 group">
                                <MoreVertical size={20} className="text-gray-600 group-hover:text-purple-600" />
                            </button>
                        </div>
                    </div>
                </div>

                {/* Messages Area */}
                <div
                    ref={messagesContainerRef}
                    className="flex-1 overflow-y-auto p-4 md:p-6 bg-gradient-to-br from-purple-50/30 to-indigo-50/30"
                >
                    <div className="space-y-4 max-w-4xl mx-auto">
                        {currentChat?.messages.map((msg) => (
                            <div
                                key={msg.id}
                                className={`flex ${msg.sender === 'me' ? 'justify-end' : 'justify-start'} animate-fadeIn`}
                            >
                                <div className="flex flex-col max-w-xs lg:max-w-md">
                                    {msg.sender === 'other' && currentChat.isGroup && (
                                        <div className="flex items-center space-x-2 mb-1 px-4">
                                            <img
                                                src={msg.senderAvatar}
                                                alt={msg.senderName}
                                                className="w-4 h-4 rounded-full"
                                            />
                                            <span className="text-xs text-purple-600 font-medium">
                                                {msg.senderName}
                                            </span>
                                        </div>
                                    )}
                                    <div
                                        className={`px-4 py-3 rounded-2xl shadow-lg backdrop-blur-sm transition-all duration-200 hover:shadow-xl relative ${msg.sender === 'me'
                                            ? 'bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-br-md'
                                            : 'bg-white/90 text-gray-800 rounded-bl-md border border-white/50'
                                            }`}
                                    >
                                        <p className="text-sm leading-relaxed">{msg.text}</p>
                                        <div className={`flex items-center justify-end mt-2 space-x-1 text-xs ${msg.sender === 'me' ? 'text-purple-100' : 'text-gray-500'}`}>
                                            <span className="font-medium">{msg.time}</span>
                                            {msg.sender === 'me' && (
                                                <>
                                                    {msg.status === 'sent' && <Check size={14} className="text-purple-200" />}
                                                    {msg.status === 'delivered' && <CheckCheck size={14} className="text-purple-200" />}
                                                    {msg.status === 'read' && <CheckCheck size={14} className="text-blue-200" />}
                                                </>
                                            )}
                                        </div>
                                        {msg.sender === 'other' && msg.status === 'unread' && (
                                            <div className="absolute -right-2 top-2 w-3 h-3 bg-red-500 rounded-full border-2 border-white"></div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                </div>

                {/* Message Input */}
                <div className="bg-white/80 backdrop-blur-xl p-4 border-t border-white/20">
                    <div className="flex items-center space-x-2 md:space-x-4 max-w-4xl mx-auto">
                        <button className="p-2 hover:bg-purple-100 rounded-xl transition-all duration-200 group">
                            <Smile size={22} className="text-gray-500 group-hover:text-purple-600" />
                        </button>
                        <button className="p-2 md:p-3 hover:bg-purple-100 rounded-xl transition-all duration-200 group">
                            <Paperclip size={22} className="text-gray-500 group-hover:text-purple-600" />
                        </button>
                        <div className="flex-1 relative">
                            <input
                                type="text"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                onKeyPress={handleKeyPress}
                                placeholder="Type your message..."
                                className="w-full px-2 py-3 md:py-4 bg-white/90 backdrop-blur-sm rounded-2xl border border-white/50 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent shadow-sm transition-all duration-200 text-gray-800 placeholder-gray-500"
                            />
                        </div>
                        <button
                            onClick={sendMessage}
                            disabled={!message.trim()}
                            className={`p-3 md:p-4 rounded-xl transition-all duration-200 shadow-lg ${message.trim()
                                ? 'bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white transform hover:scale-105'
                                : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                                }`}
                        >
                            <Send size={18} />
                        </button>
                    </div>
                </div>
            </div>

            <style jsx>{`
                @keyframes fadeIn {
                    from { opacity: 0; transform: translateY(10px); }
                    to { opacity: 1; transform: translateY(0); }
                }
                .animate-fadeIn {
                    animation: fadeIn 0.3s ease-out;
                }
                
                /* Responsive adjustments */
                @media (max-width: 767px) {
                    .w-96 {
                        width: 100%;
                    }
                }
            `}</style>
        </div>
    );
};

export default AppChatBox;