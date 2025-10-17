// Game/Game.jsx
import React, { useState } from "react";
import { initialGameState } from "./demoLogic";
import Board from "./demoBoard";

export default function Game() {
  const [gameState, setGameState] = useState(initialGameState);

  const handleEndTurn = () => {
    setGameState((prev) => ({
      ...prev,
      currentTurn: prev.currentTurn === 1 ? 2 : 1,
    }));
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "1.5rem",
        fontFamily: "sans-serif",
      }}
    >
      {/* Estado superior */}
      <h1>
        {gameState.winner
          ? `Ganó ${gameState.winner}`
          : `Turno del Jugador ${gameState.currentTurn}`}
      </h1>

      {/* Contenedor principal */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: "80%",
        }}
      >
        {/* Jugador 1 */}
        <div style={{ textAlign: "center" }}>
          <h2>{gameState.players[0].name}</h2>
          <p>Puntuación: {gameState.players[0].score}</p>
        </div>

        {/* Tablero */}
        <Board board={gameState.board} />

        {/* Jugador 2 */}
        <div style={{ textAlign: "center" }}>
          <h2>{gameState.players[1].name}</h2>
          <p>Puntuación: {gameState.players[1].score}</p>
        </div>
      </div>

      {/* Botón inferior */}
      <button
        onClick={handleEndTurn}
        style={{
          padding: "0.5rem 1rem",
          borderRadius: "8px",
          border: "none",
          background: "#78c712ff",
          color: "white",
          cursor: "pointer",
          fontSize: "1rem",
        }}
      >
        Terminar turno
      </button>
    </div>
  );
}

