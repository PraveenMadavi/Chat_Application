import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

export const CryptoService = {
    publicKey: null,
    aesKey: null,
    iv: null,

    // Initialize crypto setup
    async initialize() {
        await this.getPublicKey();
        await this.generateAesKey();
        return this;
    },

    // Get RSA public key from server
    async getPublicKey() {
        try {
            const response = await api.get('/crypto/get-public-key');
            const binaryKey = this.base64ToArrayBuffer(response.data);
            
            this.publicKey = await window.crypto.subtle.importKey(
                "spki",
                binaryKey,
                { name: "RSA-OAEP", hash: "SHA-256" },
                true,
                ["encrypt"]
            );
        } catch (error) {
            console.error("Error getting public key:", error);
            throw error;
        }
    },

    // Generate and send AES key to server
    async generateAesKey() {
        try {
            // Generate AES-CBC key
            this.aesKey = await window.crypto.subtle.generateKey(
                { name: "AES-CBC", length: 256 },
                true,
                ["encrypt", "decrypt"]
            );
            
            // Export and encrypt the AES key
            const rawKey = await window.crypto.subtle.exportKey("raw", this.aesKey);
            const encryptedKey = await window.crypto.subtle.encrypt(
                { name: "RSA-OAEP" },
                this.publicKey,
                rawKey
            );
            
            // Send to server
            await api.post('/crypto/set-aes-key', {
                encryptedAesKey: this.arrayBufferToBase64(encryptedKey)
            });
            
            // Server will set HttpOnly cookie automatically
        } catch (error) {
            console.error("Error generating AES key:", error);
            throw error;
        }
    },

    // Encrypt and send data
    async sendEncryptedData(url, payload) {
        try {
            if (!this.aesKey) {
                throw new Error("AES key not initialized");
            }
            
            // Generate fresh IV for each message
            this.iv = window.crypto.getRandomValues(new Uint8Array(16));
            
            // Convert payload to string and encrypt
            const payloadStr = JSON.stringify(payload);
            const encodedPayload = new TextEncoder().encode(payloadStr);
            const encryptedData = await window.crypto.subtle.encrypt(
                { name: "AES-CBC", iv: this.iv },
                this.aesKey,
                encodedPayload
            );
            
            // Prepare request
            const requestData = {
                iv: this.arrayBufferToBase64(this.iv),
                encryptedData: this.arrayBufferToBase64(encryptedData)
            };
            
            // Send with credentials (cookies)
            const response = await api.post(url, requestData);
            return response.data;
        } catch (error) {
            console.error("Error sending encrypted data:", error);
            throw error;
        }
    },

    // Helper methods
    arrayBufferToBase64(buffer) {
        return btoa(String.fromCharCode(...new Uint8Array(buffer)));
    },

    base64ToArrayBuffer(base64) {
        const binaryString = atob(base64);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        return bytes.buffer;
    },

    async decryptData (encryptedData) {
        try {
            if (!this.aesKey) {
                throw new Error("AES key not initialized");
            }

            // Convert base64 strings back to ArrayBuffer
            const iv = this.base64ToArrayBuffer(encryptedData.iv);
            const encryptedPayload = this.base64ToArrayBuffer(encryptedData.encryptedPayload);

            // Decrypt the payload
            const decrypted = await window.crypto.subtle.decrypt(
                { name: "AES-CBC", iv },
                this.aesKey,
                encryptedPayload
            );

            // Convert ArrayBuffer to string and parse JSON
            const decryptedStr = new TextDecoder().decode(decrypted);
            return JSON.parse(decryptedStr);
            
        } catch (error) {
            console.error("Decryption failed:", error);
            throw error;
        }
    },


};