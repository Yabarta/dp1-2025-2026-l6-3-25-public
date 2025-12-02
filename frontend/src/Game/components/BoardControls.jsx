import React from 'react';

export default function BoardControls({
  boardInstruction,
  canEditBoard,
  moveAmountValue,
  maxMoveForSource,
  onMoveAmountChange,
  quickAmountOptions,
  moveAmount,
  selectedSource,
  sourceCapacity,
  onQuickAmountClick,
  onApplyMove,
  applyDisabled,
  onCancelSelection,
  cancelDisabled,
  boardFeedback,
  lastMoveText,
}) {
  return (
    <div className="boardControls">
      <p className="boardInstruction">{boardInstruction}</p>
      {canEditBoard && (
        <>
          <div className="controlRow">
            <label htmlFor="move-amount">Unidades a mover:</label>
            <input
              id="move-amount"
              type="number"
              min="1"
              max={maxMoveForSource}
              value={moveAmountValue}
              onChange={onMoveAmountChange}
            />
            <div className="quick-amounts">
              {quickAmountOptions.map((value) => (
                <button
                  key={value}
                  type="button"
                  className={moveAmount === value ? 'active' : ''}
                  disabled={selectedSource === null || value > sourceCapacity}
                  onClick={() => onQuickAmountClick(value)}
                >
                  {value}
                </button>
              ))}
            </div>
          </div>
          <div className="controlRow controlRow--actions">
            <button type="button" onClick={onApplyMove} disabled={applyDisabled}>
              Aplicar movimiento
            </button>
            <button type="button" onClick={onCancelSelection} disabled={cancelDisabled}>
              Cancelar selección
            </button>
          </div>
        </>
      )}
      {boardFeedback && <div className="board-feedback">{boardFeedback}</div>}
      <div className="last-move-info">
        Último movimiento: <strong>{lastMoveText}</strong>
      </div>
    </div>
  );
}
