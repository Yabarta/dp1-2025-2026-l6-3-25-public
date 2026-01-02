import React from 'react';

export default function ProfileHeader({ playerData = {}, currentPlayer, username, setShowEditPopup }) {
  return (
    <div>
      <div className="profileHeader">
        <span className="profileNickname">{playerData.nickname}</span>
        {currentPlayer && currentPlayer === username && (
          <span onClick={() => setShowEditPopup(true)} className="editIcon">✏️</span>
        )}
      </div>
      <div className="profileHeaderEmail">{playerData.email}</div>
    </div>
  );
}
