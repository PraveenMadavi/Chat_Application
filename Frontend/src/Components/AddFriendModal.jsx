
// import React, { useState } from "react";
// import { X, Mail, UserPlus, Search } from "lucide-react";
// import axios from "axios";
// import { useUser } from './Conetx/UserContext';

// const AddFriendModal = ({ isOpen, onClose }) => {
//   const [email, setEmail] = useState("");
//   const [isLoading, setIsLoading] = useState(false);
//   const [error, setError] = useState("");
//   const [searchResults, setSearchResults] = useState(null);
//   const [isSearching, setIsSearching] = useState(false);
//   const { user, aesKey } = useUser();
//   const BaseURl = import.meta.env.VITE_API_BASE_URL

//   const handleSearch = async () => {
//     if (!email.trim()) {
//       setError("Please enter an email address");
//       return;
//     }

//     // Basic email validation
//     const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
//     if (!emailRegex.test(email)) {
//       setError("Please enter a valid email address");
//       return;
//     }

//     setIsSearching(true);
//     setError("");
//     setSearchResults(null);

//     try {

//       const payload = {
//         email: email
//       };
//       const config = {
//         withCredentials: true, // This ensures cookies are sent
//         headers: {
//           'Content-Type': 'application/json',
//           // Add any additional headers if needed
//           'Accept': 'application/json'
//         },
//         // Ensure cookies from domain are included
//         credentials: 'include'
//       };

//       console.log("Email stringified:", payload);
//       const url = `${BaseURl}/user/is-present`;
//       const response = await axios.post(url, payload, config);

//       console.log("Search API response:", response.data);

//       if (response.data) {
//         setSearchResults(response.data);
//       } else {
//         setError("User not found with this email address");
//       }

//     } catch (err) {
//       console.error("Search failed:", err);
//       setError(err.response?.data?.message || "Failed to search for user");
//     } finally {
//       setIsSearching(false);
//     }
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     if (!searchResults || !searchResults.exists) {
//       setError("Please search for a valid user first");
//       return;
//     }

//     setIsLoading(true);
//     try {

//       // await onAddFriend(email);
//       setEmail("");
//       setSearchResults(null);
//       onClose();
//     } catch (err) {
//       setError(err.message || "Failed to add friend");
//     } finally {
//       setIsLoading(false);
//     }
//   };

//   if (!isOpen) return null;

//   return (
//     <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
//       <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 transform transition-all duration-300">
//         <div className="flex items-center justify-between mb-6">
//           <div className="flex items-center space-x-3">
//             <div className="w-10 h-10 bg-gradient-to-r from-purple-600 to-indigo-600 rounded-full flex items-center justify-center">
//               <UserPlus size={20} className="text-white" />
//             </div>
//             <h2 className="text-xl font-semibold text-gray-900">Add Friend</h2>
//           </div>
//           <button
//             onClick={onClose}
//             className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
//           >
//             <X size={20} className="text-gray-500" />
//           </button>
//         </div>

//         <form onSubmit={handleSubmit}>
//           <div className="space-y-4">
//             <div>
//               <label className="block text-sm font-medium text-gray-700 mb-2">
//                 Search by Email
//               </label>
//               <div className="relative flex items-center">
//                 <Mail size={18} className="absolute left-3 text-gray-400" />
//                 <input
//                   type="email"
//                   value={email}
//                   onChange={(e) => setEmail(e.target.value)}
//                   placeholder="Enter email address"
//                   className="w-full pl-10 pr-16 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-200"
//                   disabled={isLoading || isSearching}
//                 />
//                 <button
//                   type="button"
//                   onClick={handleSearch}
//                   disabled={!email.trim() || isSearching}
//                   className="absolute right-3 p-1 text-purple-600 hover:text-purple-800 disabled:opacity-50"
//                 >
//                   <Search size={20} />
//                 </button>
//               </div>
//             </div>

//             {searchResults && searchResults.exists && (
//               <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
//                 <div className="flex items-center space-x-3">
//                   <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
//                     <span className="text-green-600 text-lg font-bold">
//                       {searchResults.user?.name?.charAt(0) || email.charAt(0).toUpperCase()}
//                     </span>
//                   </div>
//                   <div>
//                     <p className="font-medium text-gray-900">
//                       {searchResults.user?.name || email}
//                     </p>
//                     <p className="text-sm text-gray-500">{email}</p>
//                   </div>
//                 </div>
//               </div>
//             )}

//             {error && (
//               <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
//                 <p className="text-sm text-red-600">{error}</p>
//               </div>
//             )}

//             <div className="flex space-x-3 pt-4">
//               <button
//                 type="button"
//                 onClick={onClose}
//                 className="flex-1 py-3 px-4 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
//                 disabled={isLoading}
//               >
//                 Cancel
//               </button>
//               <button
//                 type="submit"
//                 disabled={isLoading || !searchResults?.exists}
//                 className="flex-1 py-3 px-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
//               >
//                 {isLoading ? (
//                   <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
//                 ) : (
//                   <span>Add Friend</span>
//                 )}
//               </button>
//             </div>
//           </div>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default AddFriendModal;

import React, { useState } from "react";
import { X, Mail, UserPlus, Search } from "lucide-react";
import axios from "axios";
import { useUser } from './Conetx/UserContext';

const AddFriendModal = ({ user, isOpen, onClose }) => {
  const [email, setEmail] = useState("");
  const [roomId, setRoomId] = useState("");
  // const [, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [searchResults, setSearchResults] = useState(null); //
  const [isSearching, setIsSearching] = useState(false);
  const BaseURl = import.meta.env.VITE_API_BASE_URL;

  const handleSearch = async () => {
    if (!email.trim()) {
      setError("Please enter an email address");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Please enter a valid email address");
      return;
    }

    setIsSearching(true);
    setError("");
    setSearchResults(null);

    const config = {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    };


    try {
      const payload = { email };

      const url = `${BaseURl}/user/is-present`;
      const response = await axios.post(url, payload, config);//.............................................

      console.log("Search API response:", response.data);

      if (response.data) {
        setSearchResults({
          id: response.data.id || null,
          email:response.data.email || "NA",
          username: response.data.username || 'Not available',
        });
        console.log("Search results:", searchResults);
        setError(""); // Clear any previous errors
      } else {
        setSearchResults({ exists: false });
        setError(response.data?.message || "User not found with this email address");
      }

    } catch (err) {
      console.error("Search failed:", err);
      setSearchResults(null);
      setError(err.response?.data?.message || "Failed to search for user");
    } finally {
      setIsSearching(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!searchResults || !searchResults || !searchResults) {
      setError("Please search for a valid user first");
      return;
    }
   
    setIsLoading(true);
    try {
      // Here you would typically send the friend request
      // For example:
      // const response = await axios.post(`${BaseURl}/friends/add`, {
      //   friendId: searchResults.user.id
      // }, { withCredentials: true });
      const payload2 = {
        creatorId: user.id,
        friendId: searchResults.id,
        isPrivate: true,
      };
      const config2 = {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include'
      };


      const url = `${BaseURl}/user/create-chatroom`;
      console.log("Payload for adding friend:", payload2, "URL:", url, "config:", config2);//.................................
      const response = await axios.post(url, payload2, config2);

      console.log("CREATED ROOM INFO : ", response.data);
      setRoomId(response.data.id)

      // On success:
      setEmail("");
      setSearchResults(null);
      onClose();    
    } catch (err) {
      setError(err.response?.data?.message || "Failed to add friend");
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 transform transition-all duration-300">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-r from-purple-600 to-indigo-600 rounded-full flex items-center justify-center">
              <UserPlus size={20} className="text-white" />
            </div>
            <h2 className="text-xl font-semibold text-gray-900">Add Friend</h2>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Search by Email
              </label>
              <div className="relative flex items-center">
                <Mail size={18} className="absolute left-3 text-gray-400" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter email address"
                  className="w-full pl-10 pr-16 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-200"
                  disabled={isLoading || isSearching}
                />
                <button
                  type="button"
                  onClick={handleSearch}
                  disabled={!email.trim() || isSearching}
                  className="absolute right-3 p-1 text-purple-600 hover:text-purple-800 disabled:opacity-50"
                >
                  <Search size={20} />
                </button>
              </div>
            </div>

            {searchResults ? (
              <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                    <span className="text-green-600 text-lg font-bold">

                      {searchResults.username?.charAt(0)?.toUpperCase() || email.charAt(0).toUpperCase()}
                    </span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      {searchResults.username}
                    </p>
                    <p className="text-sm text-gray-500">{email}</p>
                    <p className="text-xs text-gray-400 mt-1">ID: {searchResults.id || 'Not available'}</p>
                  </div>
                </div>
              </div>
            ) : searchResults && !searchResults.id ? (
              <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-sm text-yellow-700">User not found</p>
              </div>
            ) : null}

            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-sm text-red-600">{error}</p>
              </div>
            )}

            <div className="flex space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-3 px-4 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                disabled={isLoading}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isLoading || !searchResults | !searchResults?.id}
                className="flex-1 py-3 px-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
              >
                {isLoading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                ) : (
                  <span>Add Friend</span>
                )}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddFriendModal;