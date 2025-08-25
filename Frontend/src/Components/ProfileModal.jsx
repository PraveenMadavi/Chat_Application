import React, { useState } from 'react';
import { X, User } from 'lucide-react';

const ProfileModal = ({ user, onClose }) => {
    const [isLoading, setIsLoading] = useState(false);
    const [userData, setUserData] = useState(user);

    // Ensure user object exists and has required properties
    if (!userData) {
        return (
            <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
                    <p className="text-red-500">User data not available</p>
                    <button
                        onClick={onClose}
                        className="mt-4 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
                    >
                        Close
                    </button>
                </div>
            </div>
        );
    }

    // Safely access properties with fallbacks
    const safeUser = {
        username: userData.username || 'Not available',
        email: userData.email || 'Not available',
        profilePictureUrl: userData.profilePictureUrl || null,
        status: userData.status || 'OFFLINE',
        role: userData.role || 'USER',
        createdAt: userData.createdAt || new Date().toISOString(),
        isEmailVerified: userData.isEmailVerified || "false"
    };

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 transform transition-all duration-300">
                <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 bg-gradient-to-r from-purple-600 to-indigo-600 rounded-full flex items-center justify-center">
                            <User size={20} className="text-white" />
                        </div>
                        <h2 className="text-xl font-semibold text-gray-900">User Profile</h2>
                    </div>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                        <X size={20} className="text-gray-500" />
                    </button>
                </div>

                <div className="space-y-4">
                    <div className="flex items-center justify-center mb-4">
                        <div className="w-24 h-24 rounded-full bg-purple-100 flex items-center justify-center">
                            {safeUser.profilePictureUrl ? (
                                <img
                                    src={safeUser.profilePictureUrl}
                                    alt="Profile"
                                    className="w-full h-full rounded-full object-cover"
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src = '';
                                        e.target.parentElement.innerHTML =
                                            `<span class="text-3xl text-purple-600">${safeUser.username.charAt(0).toUpperCase()}</span>`;
                                    }}
                                />
                            ) : (
                                <span className="text-3xl text-purple-600">
                                    {safeUser.username.charAt(0).toUpperCase()}
                                </span>
                            )}
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <p className="text-sm text-gray-500">Name</p>
                            <p className="font-medium">{safeUser.username}</p>
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Email</p>
                            <p className="font-medium">{safeUser.email}</p>
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Status</p>
                            <p className="font-medium capitalize">{safeUser.status.toLowerCase()}</p>
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Role</p>
                            <p className="font-medium capitalize">{safeUser.role.toLowerCase()}</p>
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Member Since</p>
                            <p className="font-medium">
                                {new Date(safeUser.createdAt).toLocaleDateString()}
                            </p>
                        </div>
                        <div>
                            <p className="text-sm text-gray-500">Email Verified</p>
                            <p className="font-medium">
                                {safeUser.isEmailVerified === "true" ? "Yes" : "No"}
                            </p>
                        </div>
                    </div>
                </div>

                <div className="mt-6 flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ProfileModal;