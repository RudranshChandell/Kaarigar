import React, { useMemo, useState } from "react";
import ChatComponent from "../ChatComponent";

function AdminChatSection({ users }) {
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  // Show only providers and customers (not admins)
  const chatUsers = useMemo(
    () => users.filter((u) => (u.role || "").toLowerCase() !== "admin"),
    [users]
  );

  const filteredChatUsers = useMemo(() => {
    const normalized = searchTerm.trim().toLowerCase();
    if (!normalized) return chatUsers;
    return chatUsers.filter((u) => (u?.name || "").toLowerCase().includes(normalized));
  }, [chatUsers, searchTerm]);

  return (
    <div className="flex flex-col md:flex-row  h-[90vh] bg-white rounded-xl shadow-lg border border-[#1d4ed830] overflow-hidden">
      {/* User List Sidebar */}
      <div className="md:w-1/3 w-full bg-[#dbeafe] border-r border-[#1d4ed830] overflow-y-auto" >
        <div className="sticky top-0 bg-[#1d4ed8] text-white p-4 font-semibold text-lg flex items-center justify-between" >
          <span>Chats</span>
          <span className="text-sm font-normal opacity-80" >
            {chatUsers.length} users
          </span>
        </div>

        <div className="p-3 border-b border-[#1d4ed830] bg-white/60">
          <input
            type="text"
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            placeholder="Search by name"
            className="w-full rounded-lg border border-[#1d4ed830] bg-white px-3 py-2 text-sm text-gray-700 outline-none focus:border-[#1d4ed8]"
          />
        </div>

        {filteredChatUsers.length === 0 ? (
          <div className="p-4 text-gray-500 text-sm text-center" >
            No matching users
          </div>
        ) : (
          filteredChatUsers.map((u) => (
            <div
              key={u.id}
              onClick={() => setSelectedUser(u)}
              className={`p-4 border-b border-[#1d4ed830] cursor-pointer transition-colors duration-200 ${
                selectedUser?.id === u.id
                  ? "bg-[#1d4ed8] text-white"
                  : "hover:bg-[#dbeafe] text-gray-800"
              }`}
            >
              <div className="flex justify-between items-center" >
                <div>
                  <p className="font-semibold text-base">{u.name}</p>
                  <p
                    className={`text-xs ${
                      selectedUser?.id === u.id
                        ? "text-white/70"
                        : "text-gray-600"
                    }`}
                  >
                    {u.role}
                  </p>
                </div>
                <span
                  className={`w-3 h-3 rounded-full ${
                    selectedUser?.id === u.id
                      ? "bg-green-300"
                      : "bg-gray-300"
                  }`}
                  title="Online status"
                ></span>
              </div>
            </div>
          ))
        )}
      </div>

     {/* Right panel: Chat area */}
  <div className="flex-1 flex flex-col bg-white h-[30rem]">
  <div className="flex flex-col flex-grow border-l border-[#1d4ed830]">
    {selectedUser ? (
      <>
        {/* Header */}
        

        {/* ChatComponent — full height below header */}
        <div className="flex-1 h-96">
          <ChatComponent receiverId={selectedUser.id}  />
        </div>
      </>
    ) : (
      <div className="flex items-center justify-center flex-1 text-gray-500">
        Select a user to start chatting
      </div>
    )}
  </div>
</div>
    </div>
  );
}
export default AdminChatSection;