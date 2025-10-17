// Game/Board.jsx
import React from "react";

export default function Board({ board }) {
  return (
    <div
      className="board"
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "1rem",
      }}
    >
      {/* Fila superior (2 discos) */}
      <div style={{ display: "flex", gap: "1rem" }}>
        <Disco disco={board[0]} />
        <Disco disco={board[1]} />
      </div>

      {/* Fila central (3 discos) */}
      <div style={{ display: "flex", gap: "1rem" }}>
        <Disco disco={board[2]} />
        <Disco disco={board[3]} />
        <Disco disco={board[4]} />
      </div>

      {/* Fila inferior (2 discos) */}
      <div style={{ display: "flex", gap: "1rem" }}>
        <Disco disco={board[5]} />
        <Disco disco={board[6]} />
      </div>
    </div>
  );
}

function Disco({ disco }) {
  return (
    <div
      style={{
        width: 80,
        height: 80,
        borderRadius: "50%",
        backgroundColor: "#e0e0e0",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        fontSize: 12,
      }}
    >
      <strong>{disco.id}</strong>
      <div>
        <span>J1: {disco.j1}</span> | <span>J2: {disco.j2}</span>
      </div>
    </div>
  );
}
