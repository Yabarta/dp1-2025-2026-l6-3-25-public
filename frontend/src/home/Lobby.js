import React from 'react';
import '../static/css/home/home.css';

export default function Lobby({ roomCode, onBackToMenu, onStartGame }) {
    return (
        <div className="home-page-container">
            <div className="main-menu">
                <h1>Sala Privada</h1>
                <div className="private-room-content">
                    <h2>Código de la Partida:</h2>
                    <div className="room-code-display">{roomCode}</div>
                    <p>Comparte este código para que la otra persona se una a la partida</p>
                    <div className="waiting-message">
                        <p>Esperando al segundo jugador...</p>
                        <div className="loading-spinner"></div>
                    </div>
                    <div className="room-buttons">
                        <button className="menu-button" onClick={onStartGame}>
                            Iniciar Partida
                        </button>
                        <button className="back-button" onClick={onBackToMenu}>
                            Volver al Menú
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}