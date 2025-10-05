import React, { useState } from 'react';
import '../static/css/game/gameScreen.css';
import ExitModal from '../components/modal/ExitModal';

export default function GameScreen ({ roomCode, onBackToMenu }) {
  const [waitingForPlayer, setWaitingForPlayer] = useState(true);
  const [exitGame, setExitGame] = useState(null);

  const handleExit = () => {
    setExitGame(null)
    onBackToMenu()
  }

  return (
    <div className="gameScreenContainer">
      <ExitModal 
        text='¿Seguro que quieres abandonar la partida?'
        isVisible={exitGame !== null}
        onConfirm={() => {
          handleExit()
        }}
        onCancel={() => {setExitGame(null)}}>

      </ExitModal>
      <div className="chatPanel">
        <div className="chatTitle">CHAT</div>
        {waitingForPlayer && (
          <div style={{ textAlign: 'center' }}>
            <p style={{ margin: '0.5rem 0', fontSize: '0.9rem' }}>Código de partida:</p>
            <div className="roomCode">{roomCode}</div>
            <p>Comparte este código para que se una otro jugador</p>
          </div>
        )}
      </div>

      <div className="mainPanel">
        <div className="topBar">
          <span className="timer">Tiempo</span>
          {waitingForPlayer && (
            <button className="back" onClick={() => { setExitGame(true) }}>
              Volver al Menú
            </button>
          )}
        </div>
        <div className="playersPanels">
          <div className="playerPanel left">
            <div className="playerInfo">P1</div>
            <div className="playerBar"></div>
            <div className="playerAvatar"></div>
          </div>
          <div className="boardPanel">
            {waitingForPlayer ? (
              <div className="waitingMessage">
                <h2>Esperando al segundo jugador...</h2>
                <div className="loadingSpinner"></div>
                <p>Código: <strong>{roomCode}</strong></p>
              </div>
            ) : (
              <div>Aquí irá el tablero</div>
            )}
          </div>
          <div className="playerPanel right">
            <div className="playerInfo">P2</div>
            <div className="playerBar"></div>
            <div className="playerAvatar"></div>
          </div>
        </div>
        <div className="turnPanel">
          <button className="endTurn">
            Terminar turno
          </button>
        </div>
      </div>

      <div className="turnsPanel">
        <div className="turnsTitle">Turnos</div>
        <div className="turnsList">
        </div>
        <div className="turnsCount">1/4</div>
      </div>
    </div>
  );
}