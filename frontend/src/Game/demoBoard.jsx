
import React from "react";

export default function Board({ board, onDiscoClick, selectedDisc, playerStyles = [{ color: '#c42323' }, { color: '#2333c4' }] }) {
  
  return (
    <div className="board"
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        
      }}>
      <div style={{ display: "flex" }}>
        <Disco disco={board[0]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
        <Disco disco={board[1]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
      </div>

      <div style={{ display: "flex" }}>
      <Disco disco={board[2]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
      <Disco disco={board[3]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
      <Disco disco={board[4]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
      </div>

      <div style={{ display: "flex"}}>
        <Disco disco={board[5]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
        <Disco disco={board[6]} playerStyles={playerStyles} onDiscoClick={onDiscoClick} selectedDisc={selectedDisc} />
      </div>
    </div>
  );
}

function Disco({ disco, playerStyles = [{ color: '#c42323' }, { color: '#2333c4' }], onDiscoClick = () => {}, selectedDisc }) {
  const j1Style = { color: playerStyles[0].color, fontWeight: 600, marginRight: 6 };
  const j2Style = { color: playerStyles[1].color, fontWeight: 600, marginLeft: 6 };
  const isSelected = selectedDisc === disco.id;

  const specialColor = disco.id === 2 ? playerStyles[0].color : disco.id === 4 ? playerStyles[1].color : null;
  const hexSize = 170;
  const hexStyle = {
    width: hexSize,
    height: hexSize, 
    position: 'relative',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: onDiscoClick ? 'pointer' : 'default',
    boxShadow: isSelected ? '0 0 0 6px rgba(0, 110, 24, 0.25)' : undefined,
  };

  const hexInner = {
    width: '100%',
    height: '100%',
    background: specialColor || '#f0f0f0',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  color: '#222',
    fontFamily: 'Poppins, Arial, sans-serif',
    fontSize: 14,
    clipPath: 'polygon(25% 6.7%, 75% 6.7%, 100% 50%, 75% 93.3%, 25% 93.3%, 0% 50%)',
    transform: 'rotate(90deg)', // rotate the hexagon shape
    filter: 'brightness(1.15)'
  };

  // To glue hexes top-to-bottom, use negative margin on alternate rows when rendering layout
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', margin: -6 }}>
      <div onClick={() => onDiscoClick && onDiscoClick(disco.id)} style={hexStyle}>
        <div style={{ ...hexInner, color: 'black' }}>
          {/* rotate content back so text is readable */}
          <div style={{ transform: 'rotate(-90deg)', textAlign: 'center', color: 'black' }}>
            <div style={{ fontWeight: 600 }}>{disco.id}</div>
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
