
import React from "react";

const DEFAULT_LAST_MOVE = { source: null, target: null };

export default function Board({
  board,
  onDiscoClick,
  selectedDisc = null,
  selectedSource = null,
  selectedTarget = null,
  lastMove = DEFAULT_LAST_MOVE,
  disabled = false,
  playerStyles = [{ color: '#c42323' }, { color: '#2333c4' }],
}) {
  const safeBoard = Array.from({ length: 7 }, (_, idx) => board?.[idx] ?? { id: idx, j1: 0, j2: 0 });
  const activeSource = selectedSource ?? selectedDisc ?? null;
  const activeTarget = selectedTarget ?? null;

  return (
    <div
      className="board"
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    >
      <div style={{ display: 'flex' }}>
        <Disco
          disco={safeBoard[0]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[0].id,
            isTarget: activeTarget === safeBoard[0].id,
            isLastSource: lastMove?.source === safeBoard[0].id,
            isLastTarget: lastMove?.target === safeBoard[0].id,
          }}
        />
        <Disco
          disco={safeBoard[1]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[1].id,
            isTarget: activeTarget === safeBoard[1].id,
            isLastSource: lastMove?.source === safeBoard[1].id,
            isLastTarget: lastMove?.target === safeBoard[1].id,
          }}
        />
      </div>

      <div style={{ display: 'flex' }}>
        <Disco
          disco={safeBoard[2]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[2].id,
            isTarget: activeTarget === safeBoard[2].id,
            isLastSource: lastMove?.source === safeBoard[2].id,
            isLastTarget: lastMove?.target === safeBoard[2].id,
          }}
        />
        <Disco
          disco={safeBoard[3]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[3].id,
            isTarget: activeTarget === safeBoard[3].id,
            isLastSource: lastMove?.source === safeBoard[3].id,
            isLastTarget: lastMove?.target === safeBoard[3].id,
          }}
        />
        <Disco
          disco={safeBoard[4]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[4].id,
            isTarget: activeTarget === safeBoard[4].id,
            isLastSource: lastMove?.source === safeBoard[4].id,
            isLastTarget: lastMove?.target === safeBoard[4].id,
          }}
        />
      </div>

      <div style={{ display: 'flex' }}>
        <Disco
          disco={safeBoard[5]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[5].id,
            isTarget: activeTarget === safeBoard[5].id,
            isLastSource: lastMove?.source === safeBoard[5].id,
            isLastTarget: lastMove?.target === safeBoard[5].id,
          }}
        />
        <Disco
          disco={safeBoard[6]}
          playerStyles={playerStyles}
          onDiscoClick={onDiscoClick}
          disabled={disabled}
          highlight={{
            isSource: activeSource === safeBoard[6].id,
            isTarget: activeTarget === safeBoard[6].id,
            isLastSource: lastMove?.source === safeBoard[6].id,
            isLastTarget: lastMove?.target === safeBoard[6].id,
          }}
        />
      </div>
    </div>
  );
}

function Disco({
  disco,
  playerStyles = [{ color: '#c42323' }, { color: '#2333c4' }],
  onDiscoClick = () => {},
  disabled = false,
  highlight = {},
}) {
  const { isSource, isTarget, isLastSource, isLastTarget } = highlight;
  const j1Style = { color: playerStyles[0].color, fontWeight: 600, marginRight: 6 };
  const j2Style = { color: playerStyles[1].color, fontWeight: 600, marginLeft: 6 };
  const ring = [];
  if (isSource) {
    ring.push('0 0 0 8px rgba(237, 255, 71, 0.8)');
  }
  if (isTarget) {
    ring.push('0 0 0 12px rgba(255, 61, 229, 0.8)');
  }
  if (isLastSource) {
    ring.push('0 0 0 0px rgba(185, 163, 40, 0.8)');
  }
  if (isLastTarget) {
    ring.push('0 0 0 0px rgba(142, 69, 211, 0.8)');
  }

  const j1Count = Number(disco?.j1 ?? 0);
  const j2Count = Number(disco?.j2 ?? 0);
  let fillColor = '#f0f0f0';
  if (j1Count > j2Count) {
    fillColor = playerStyles[0].color;
  } else if (j2Count > j1Count) {
    fillColor = playerStyles[1].color;
  }
  const hexSize = 170;
  const hexStyle = {
    width: hexSize,
    height: hexSize,
    position: 'relative',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: disabled ? 'default' : 'pointer',
    boxShadow: ring.length ? ring.join(', ') : undefined,
    transition: 'box-shadow 180ms ease',
  };

  const hexInner = {
    width: '100%',
    height: '100%',
    background: fillColor,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#222',
    fontFamily: 'Poppins, Arial, sans-serif',
    fontSize: 14,
    clipPath: 'polygon(25% 6.7%, 75% 6.7%, 100% 50%, 75% 93.3%, 25% 93.3%, 0% 50%)',
    transform: 'rotate(90deg)',
    filter: 'brightness(1.05)',
  };

  if (!disco) {
    return null;
  }
  const plateNumber = Number(disco.id ?? 0) + 1;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', margin: -6 }}>
      <div onClick={() => !disabled && onDiscoClick && onDiscoClick(disco.id)} style={hexStyle}>
        <div style={{ ...hexInner, color: 'black' }}>
          <div style={{ transform: 'rotate(-90deg)', textAlign: 'center', color: 'black' }}>
            <div style={{ fontWeight: 600 }}>{plateNumber}</div>
            <div style={{ marginTop: 6 }}>
              <span style={{ ...j1Style, color: 'black' }}>{'J1: '}</span>
              <span style={{ fontWeight: 700 }}>{disco.j1}</span>
              <span style={{ margin: '0 6px', color: 'black' }}>|</span>
              <span style={{ ...j2Style, color: 'black' }}>{' J2: '}</span>
              <span style={{ fontWeight: 700 }}>{disco.j2}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
