import React from 'react';
import Text  from './Text'
import * as GlobalStyles from './GlobalStyles';

export default function ExitModal(props) {
  if (!props.isVisible) return null; // En web no hay "visible" de Modal

  return (
    <div style={styles.overlay}>
      <div style={styles.modalView}>
        <Text>
          ¿Seguro que quieres abandonar la partida?
        </Text>
        <br></br>
        {props.children}

        <button
          onClick={props.onCancel}
          style={{
            ...styles.actionButton,
            backgroundColor: GlobalStyles.brandBlue,
          }}
        >
          <Text>Cancelar</Text>
        </button>

        <button
          onClick={props.onConfirm}
          style={{
            ...styles.actionButton,
            backgroundColor: GlobalStyles.brandPrimary,
          }}
        >
          <Text>Abandonar</Text>
        </button>
      </div>
    </div>
  );
}

const styles = {
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100vw',
    height: '100vh',
    backgroundColor: 'rgba(0, 0, 0, 0.4)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
  },
  modalView: {
    border: '1px solid rgba(144, 224, 153, 0.8)',
    backgroundColor: 'rgba(1, 28, 4, 0.96)',
    borderRadius: 20,
    padding: 35,
    textAlign: 'center',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.25)',
    width: '90%',
    maxWidth: '400px',
  },
  actionButton: {
    color: 'white',
    border: 'none',
    borderRadius: 8,
    height: 40,
    marginTop: 12,
    margin: '1%',
    padding: 10,
    alignSelf: 'center',
    width: '50%',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
  },
  text: {
    fontSize: 16,
    color: 'white',
    textAlign: 'center',
    marginLeft: 5,
  },
};
