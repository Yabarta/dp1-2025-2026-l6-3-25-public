// Agrega esto arriba en ExitModal.js o en un archivo aparte
export default function Text({ children, textStyle }) {
  // Permite tanto objeto como array de estilos
  const style = Array.isArray(textStyle)
    ? Object.assign({}, ...textStyle)
    : textStyle;
  return <span style={style}>{children}</span>;
}