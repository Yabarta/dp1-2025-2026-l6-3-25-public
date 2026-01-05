import React from 'react';

const DEFAULT_META = { label: '?', description: 'Desconocido', className: 'turnPhase--default' };

export default function TurnTimeline({
  currentPhaseMeta,
  activeRoundIndex,
  activeRoundPhases,
  currentTurnIndex,
  totalTurns,
  turnTrackOffset,
  turnTrackRef,
  turnPhaseMeta,
  roundSize,
}) {
  const safeDisplayTurn = currentTurnIndex < 0 ? 0 : Math.min(currentTurnIndex, totalTurns - 1);
  const turnNumberLabel = currentTurnIndex < 0 ? 0 : safeDisplayTurn + 1;

  return (
    <>
      <div className="turnSummary">Turno {turnNumberLabel % 10} · Ronda {activeRoundIndex + 1}</div>
      {currentPhaseMeta && <small>{currentPhaseMeta.description}</small>}
      <div className="turnsList">
        <div className="turnTimeline">
          <div className="turnRound" key={`round-${activeRoundIndex}`}>
            <div className="turnRoundTrack" ref={turnTrackRef} style={{ transform: `translateY(${turnTrackOffset}px)` }}>
              {activeRoundPhases.map((phase, phaseIdx) => {
                const globalIndex = activeRoundIndex * roundSize + phaseIdx;
                const meta = turnPhaseMeta[phase] || DEFAULT_META;
                const circleClasses = ['turnPhase', meta.className];
                const wrapperClasses = ['turnPhaseItem'];

                if (currentTurnIndex > globalIndex) {
                  circleClasses.push('is-complete');
                  wrapperClasses.push('is-complete');
                } else if (currentTurnIndex === globalIndex) {
                  circleClasses.push('is-current');
                  wrapperClasses.push('is-current');
                }

                const showConnector = phaseIdx < activeRoundPhases.length - 1;

                return (
                  <div
                    key={`phase-${globalIndex}`}
                    className={wrapperClasses.join(' ')}
                    title={`Ronda ${activeRoundIndex + 1} · ${meta.description}`}
                  >
                    <div className={circleClasses.join(' ')}>
                      <span className="turnPhaseLabel">
                        {meta.label}
                        <small>{globalIndex + 1}</small>
                      </span>
                    </div>
                    {showConnector && <span className="turnPhaseConnector" />}
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
