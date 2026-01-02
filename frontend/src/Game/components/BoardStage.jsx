import React from 'react';
import Board from '../demoBoard';
import BoardControls from './BoardControls';

export default function BoardStage({ waitingForPlayer, roomCode, boardProps, controlsProps }) {
  if (waitingForPlayer) {
    return (
      <div className="waitingStage">
        <h2>Esperando al segundo jugador...</h2>
        <div className="loadingSpinner" />
        <p>
          Código: <strong>{roomCode}</strong>
        </p>
      </div>
    );
  }

  return (
    <>
      <div className="boardWrapper">
        <Board {...boardProps} />
      </div>
      <BoardControls {...controlsProps} />
    </>
  );
}
