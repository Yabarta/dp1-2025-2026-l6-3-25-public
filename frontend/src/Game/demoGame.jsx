// Game/Game.jsx
import React, { useState, useEffect } from "react";
import { initialGameState, turnOrder, nextPhase } from "./demoLogic";
import Board from "./demoBoard";

export default function Game() {
  const [gameState, setGameState] = useState({
    ...initialGameState,
    turnOrder: turnOrder,
  });
  const [selectedOrigin, setSelectedOrigin] = useState(null);
  const [selectedDest, setSelectedDest] = useState(null);
  const [moveAmount, setMoveAmount] = useState(1);

  // estilos por jugador (puedes cambiarlos aquí)
  const playerStyles = [
    { color: '#c42323', nameColor: '#c42323' }, // jugador 1: rojo
    { color: '#2333c4', nameColor: '#2333c4' }, // jugador 2: azul
  ];

  const currentPhase = gameState.turnOrder[gameState.currentPhaseIndex];

  // Determina el jugador actual (1 o 2)
  const currentPlayer = currentPhase === 'J1' ? 1 : currentPhase === 'J2' ? 2 : null;

  // Handler para seleccionar discos
  const handleDiscoClick = (id) => {
    if (selectedOrigin === null) {
      setSelectedOrigin(id);
      setSelectedDest(null);
    } else if (selectedOrigin !== null && selectedDest === null && id !== selectedOrigin) {
      setSelectedDest(id);
    } else {
      // Si ya hay origen y destino, reinicia selección
      setSelectedOrigin(id);
      setSelectedDest(null);
    }
  };

  // Handler para mover bacterias
  const handleMove = () => {
    if (selectedOrigin === null || selectedDest === null || !currentPlayer) return;
    // Validar cantidad
    const originDisco = gameState.board.find(d => d.id === selectedOrigin);
    const destDisco = gameState.board.find(d => d.id === selectedDest);
    const key = currentPlayer === 1 ? 'j1' : 'j2';
    if (!originDisco || !destDisco || originDisco[key] < moveAmount) return;

    // Actualizar el estado
    const newBoard = gameState.board.map(disco => {
      if (disco.id === selectedOrigin) {
        return { ...disco, [key]: disco[key] - moveAmount };
      } else if (disco.id === selectedDest) {
        return { ...disco, [key]: disco[key] + moveAmount };
      }
      return disco;
    });
    setGameState({ ...gameState, board: newBoard });
    setSelectedOrigin(null);
    setSelectedDest(null);
    setMoveAmount(1);
  };

  const handleEndTurn = () => {
    setGameState((prev) => nextPhase(prev));
    setSelectedOrigin(null);
    setSelectedDest(null);
    setMoveAmount(1);
  };

  useEffect(() => {
    if (!gameState.winner) {
      if (gameState.players[0].score >= 9) {
        setGameState(prev => ({ ...prev, winner: gameState.players[1].name }));
      } else if (gameState.players[1].score >= 9) {
        setGameState(prev => ({ ...prev, winner: gameState.players[0].name }));
      }
    }
  }, [gameState.players[0].score, gameState.players[1].score, gameState.winner]);

  const demoContainerStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 2fr 2fr 1fr',
    gap: '0.5rem',
    alignItems: 'start',
    fontFamily: 'Poppins, Arial, sans-serif',
    fontSize: '0.85rem' 
  };

  const centerColumnStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem'
  };

  // backgrounds: center pistachio, left overlay, right light gray
  const pistachio = '#cfeecf';
  const leftOverlay = 'rgba(0,0,0,0.25)';
  const rightGray = '#f5f5f649';

  return (
    <div style={demoContainerStyle}>
      <div style={{ background: pistachio, position: 'relative' }}>
        <div style={{ position: 'absolute', inset: 0, background: leftOverlay }} />
      </div>
      <div style={{ ...centerColumnStyle, gridColumn: '2 / span 2', background: pistachio, padding: 12 }}>
        {/* Estado superior */}
        <h1>
          {gameState.winner
            ? `Ganó ${gameState.winner}`
            : `Fase actual: ${currentPhase}`}
        </h1>

        {/* Movimiento de bacterias UI */}
        <div style={{ marginBottom: '1rem', minHeight: 40 }}>
          {currentPlayer && selectedOrigin !== null && selectedDest === null && (
            <div style={{ color: '#333' }}>
              Selecciona disco destino para mover bacterias desde <b>{selectedOrigin}</b>.
              <br />
              <label>
                Cantidad a mover:
                <input
                  type="number"
                  min={1}
                  max={4}
                  value={moveAmount}
                  onChange={e => setMoveAmount(Math.max(1, Math.min(4, Number(e.target.value))))}
                  style={{ width: 40, marginLeft: 8 }}
                />
              </label>
            </div>
          )}
          {currentPlayer && selectedOrigin !== null && selectedDest !== null && (
            <div>
              <span style={{ color: '#333' }}>
                Mover <b>{moveAmount}</b> bacterias de <b>{selectedOrigin}</b> a <b>{selectedDest}</b> para J{currentPlayer}
              </span>
              <button
                onClick={handleMove}
                style={{ marginLeft: 12, padding: '0.3rem 1rem', borderRadius: 6, border: 'none', background: '#23c483', color: 'white', fontWeight: 600, cursor: 'pointer' }}
              >
                Confirmar movimiento
              </button>
            </div>
          )}
        </div>

        {/* Contenedor principal en dos columnas centrales */}
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ textAlign: 'center', width: '20%' }}>
            <h2 style={{ color: playerStyles[0].nameColor, margin: 0 }}>{gameState.players[0].name}</h2>
            <p style={{ color: playerStyles[0].color, margin: 0 }}>Puntuación: {gameState.players[0].score}</p>
          </div>

          <div style={{ width: '60%', display: 'flex', justifyContent: 'center' }}>
            <Board
              board={gameState.board}
              playerStyles={playerStyles}
              onDiscoClick={id => {
                if (selectedOrigin === null || (selectedOrigin !== null && selectedDest !== null)) {
                  setSelectedOrigin(id);
                  setSelectedDest(null);
                } else if (selectedOrigin !== null && selectedDest === null && id !== selectedOrigin) {
                  setSelectedDest(id);
                }
              }}
              selectedDisc={selectedDest !== null ? selectedDest : selectedOrigin}
            />
          </div>

          <div style={{ textAlign: 'center', width: '20%' }}>
            <h2 style={{ color: playerStyles[1].nameColor, margin: 0 }}>{gameState.players[1].name}</h2>
            <p style={{ color: playerStyles[1].color, margin: 0 }}>Puntuación: {gameState.players[1].score}</p>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}>
          <button
            onClick={handleEndTurn}
            style={{
              padding: "1rem 2rem",
              borderRadius: "8px",
              border: "none",
              background: "#78c712ff",
              color: "white",
              cursor: "pointer",
              fontSize: "1.2rem",
              fontFamily: 'Poppins, Arial, sans-serif'
            }}>
            Terminar turno
          </button>
        </div>
      </div>

      <div style={{ background: rightGray }} />
    </div>
  );
}
