import React, { useState } from 'react';
import '../static/css/home/joinGameScreen.css';
import { ToastContainer,toast } from 'react-toastify';

export default function JoinGameScreen({ onBackToMenu }) {
  const [roomCode, setRoomCode] = useState('');

  const handleJoinGame = () => {
    if (roomCode.trim().toUpperCase() === '' || roomCode.trim().length !== 4 || !/^[A-Z]{4}$/.test(roomCode.trim().toUpperCase())) {
      toast.error(`Código de sala inválido. Asegúrate de que tenga 4 letras mayúsculas.`);
      return;
    } else {
      toast.success(`Unido a la partida con código: ${roomCode.trim().toUpperCase()}`);
    }
  };

  return (
    <div className="joinGameContainer">
      <h1 className='headText'> Unirse a una Partida</h1>
      <input
        type="text"
        value={roomCode}
        onChange={(c) => setRoomCode(c.target.value)}
        placeholder="Código de la sala"
      />
      <div className='buttonRow'>
        <button className="joinButton" onClick={handleJoinGame}>Unirse</button>
        <button className="backButton" onClick={onBackToMenu}>Volver al menú</button>
      </div>
      <ToastContainer />
    </div>
  );
}
