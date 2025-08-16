import React from "react";

const FullScreenLoader = () => {
    return (
        <div className="fixed inset-0 flex items-center justify-center bg-gradient-to-br from-slate-900/95 via-purple-900/90 to-slate-900/95 backdrop-blur-md z-50 cursor-wait">
            {/* Animated Background Particles */}
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
                <div className="absolute w-2 h-2 bg-blue-400/40 rounded-full top-[20%] left-[10%] animate-float"></div>
                <div className="absolute w-1 h-1 bg-purple-400/60 rounded-full top-[60%] left-[80%] animate-float-delayed"></div>
                <div className="absolute w-1.5 h-1.5 bg-cyan-400/50 rounded-full bottom-[30%] left-[15%] animate-pulse-glow"></div>
                <div className="absolute w-1 h-1 bg-pink-400/70 rounded-full top-[40%] right-[20%] animate-bounce-slow"></div>
                <div className="absolute w-2 h-2 bg-indigo-400/30 rounded-full bottom-[60%] right-[30%] animate-float"></div>

                {/* Floating Orbs */}
                <div className="absolute w-32 h-32 bg-gradient-to-br from-blue-500/10 to-purple-500/5 rounded-full top-[10%] left-[5%] animate-float blur-2xl"></div>
                <div className="absolute w-24 h-24 bg-gradient-to-br from-purple-500/10 to-pink-500/5 rounded-full bottom-[20%] right-[10%] animate-float-reverse blur-2xl"></div>
            </div>

            {/* Main Loader Container */}
            <div className="relative flex flex-col items-center">
                {/* Outer Ring */}
                <div className="relative">
                    <div className="w-36 h-36 border-4 border-transparent border-t-blue-500 border-r-purple-500 rounded-full animate-spin-slow opacity-20"></div>

                    {/* Middle Ring */}
                    <div className="absolute inset-4 w-28 h-28 border-4 border-transparent border-t-purple-500 border-r-pink-500 rounded-full animate-spin-reverse"></div>

                    {/* Inner Ring with Glow */}
                    <div className="absolute inset-8 w-20 h-20 border-4 border-transparent border-t-cyan-400 border-r-blue-400 rounded-full animate-spin-fast shadow-lg shadow-cyan-400/30"></div>

                    {/* Center Core */}
                    <div className="absolute inset-12 w-12 h-12 bg-gradient-to-br from-blue-500 via-purple-500 to-pink-500 rounded-full animate-pulse-core shadow-2xl shadow-purple-500/50 flex items-center justify-center">
                        <div className="w-6 h-6 bg-white/20 rounded-full animate-ping"></div>
                    </div>

                    {/* Orbiting Dots */}
                    <div className="absolute inset-0 w-36 h-36 animate-orbit">
                        <div className="absolute top-0 left-1/2 transform -translate-x-1/2 w-3 h-3 bg-gradient-to-r from-blue-400 to-cyan-400 rounded-full shadow-lg shadow-blue-400/50"></div>
                    </div>

                    <div className="absolute inset-0 w-36 h-36 animate-orbit-reverse">
                        <div className="absolute bottom-0 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full shadow-lg shadow-purple-400/50"></div>
                    </div>
                </div>

                {/* Loading Text */}
                <div className="mt-8 text-center space-y-2">
                    <h3 className="text-white text-xl font-semibold animate-pulse-text">
                        Loading
                        <span className="inline-block animate-bounce-dots">.</span>
                        <span className="inline-block animate-bounce-dots" style={{ animationDelay: '0.2s' }}>.</span>
                        <span className="inline-block animate-bounce-dots" style={{ animationDelay: '0.4s' }}>.</span>
                    </h3>
                    <p className="text-white/60 text-sm animate-fade-in-out">Please wait while we process your request</p>
                </div>

                {/* Progress Bar */}
                <div className="mt-6 w-64 h-1 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-full animate-progress shadow-lg shadow-purple-500/30"></div>
                </div>

                {/* Loading Percentage (Optional Animation) */}
                <div className="mt-3 text-white/70 text-xs font-medium animate-count-up">
                    Initializing...
                </div>
            </div>

            {/* Bottom Wave Animation */}
            <div className="absolute bottom-0 left-0 right-0 h-32 overflow-hidden opacity-20">
                <svg className="absolute bottom-0 w-full h-32 animate-wave" viewBox="0 0 1200 120" preserveAspectRatio="none">
                    <path d="M0,50 C150,100 350,0 500,50 C650,100 850,0 1000,50 C1100,80 1200,20 1200,50 L1200,120 L0,120 Z" fill="url(#waveGradient)"></path>
                    <defs>
                        <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" stopColor="#3b82f6" stopOpacity="0.3" />
                            <stop offset="50%" stopColor="#8b5cf6" stopOpacity="0.2" />
                            <stop offset="100%" stopColor="#ec4899" stopOpacity="0.3" />
                        </linearGradient>
                    </defs>
                </svg>
            </div>

            <style>{`
                @keyframes spin-slow {
                    from { transform: rotate(0deg); }
                    to { transform: rotate(360deg); }
                }
                
                @keyframes spin-reverse {
                    from { transform: rotate(360deg); }
                    to { transform: rotate(0deg); }
                }
                
                @keyframes spin-fast {
                    from { transform: rotate(0deg); }
                    to { transform: rotate(360deg); }
                }
                
                @keyframes pulse-core {
                    0%, 100% { transform: scale(1); opacity: 1; }
                    50% { transform: scale(1.1); opacity: 0.8; }
                }
                
                @keyframes orbit {
                    from { transform: rotate(0deg); }
                    to { transform: rotate(360deg); }
                }
                
                @keyframes orbit-reverse {
                    from { transform: rotate(360deg); }
                    to { transform: rotate(0deg); }
                }
                
                @keyframes float {
                    0%, 100% { transform: translateY(0px) scale(1); }
                    50% { transform: translateY(-10px) scale(1.05); }
                }
                
                @keyframes float-delayed {
                    0%, 100% { transform: translateY(0px) scale(1); }
                    50% { transform: translateY(-15px) scale(1.1); }
                }
                
                @keyframes float-reverse {
                    0%, 100% { transform: translateY(0px) rotate(0deg); }
                    50% { transform: translateY(-20px) rotate(180deg); }
                }
                
                @keyframes pulse-glow {
                    0%, 100% { opacity: 0.3; transform: scale(1); }
                    50% { opacity: 1; transform: scale(1.2); }
                }
                
                @keyframes bounce-slow {
                    0%, 100% { transform: translateY(0px); }
                    50% { transform: translateY(-8px); }
                }
                
                @keyframes bounce-dots {
                    0%, 100% { transform: translateY(0px); }
                    50% { transform: translateY(-5px); }
                }
                
                @keyframes pulse-text {
                    0%, 100% { opacity: 1; }
                    50% { opacity: 0.7; }
                }
                
                @keyframes fade-in-out {
                    0%, 100% { opacity: 0.6; }
                    50% { opacity: 1; }
                }
                
                @keyframes progress {
                    0% { transform: translateX(-100%); }
                    50% { transform: translateX(0%); }
                    100% { transform: translateX(100%); }
                }
                
                @keyframes wave {
                    0% { transform: translateX(0px); }
                    100% { transform: translateX(-100px); }
                }
                
                .animate-spin-slow { animation: spin-slow 3s linear infinite; }
                .animate-spin-reverse { animation: spin-reverse 2s linear infinite; }
                .animate-spin-fast { animation: spin-fast 1s linear infinite; }
                .animate-pulse-core { animation: pulse-core 2s ease-in-out infinite; }
                .animate-orbit { animation: orbit 4s linear infinite; }
                .animate-orbit-reverse { animation: orbit-reverse 6s linear infinite; }
                .animate-float { animation: float 3s ease-in-out infinite; }
                .animate-float-delayed { animation: float-delayed 3s ease-in-out infinite; animation-delay: 1s; }
                .animate-float-reverse { animation: float-reverse 8s ease-in-out infinite; }
                .animate-pulse-glow { animation: pulse-glow 2s ease-in-out infinite; }
                .animate-bounce-slow { animation: bounce-slow 2s ease-in-out infinite; }
                .animate-bounce-dots { animation: bounce-dots 1s ease-in-out infinite; }
                .animate-pulse-text { animation: pulse-text 2s ease-in-out infinite; }
                .animate-fade-in-out { animation: fade-in-out 3s ease-in-out infinite; }
                .animate-progress { animation: progress 2s ease-in-out infinite; }
                .animate-wave { animation: wave 3s ease-in-out infinite; }
            `}</style>
        </div>
    );
};

export default FullScreenLoader;