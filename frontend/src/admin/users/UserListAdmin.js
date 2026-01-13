import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import ConfirmDeleteModal from "../../components/modal/ConfirmDeleteModal";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [nombreBuscado , setNombre] = useState("");
  const [botonAll, setBotonAll] = useState(true);
  const [botonAdmin, setBotonAdmin] = useState(false);
  const [botonPlayer, setBotonPlayer] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [users, setUsers] = useState({ content: [], totalPages: 1, totalElements: 0 });
  const [alerts, setAlerts] = useState([]);
  const [confirmVisible, setConfirmVisible] = useState(false);
  const [toDelete, setToDelete] = useState({ id: null, username: "" });

  const authParam = botonAdmin ? "ADMIN" : botonPlayer ? "PLAYER" : null;

  const fetchUsers = (page = currentPage, auth = authParam) => {
    const apiUrl = `/api/v1/users?page=${page - 1}&size=10${auth ? `&auth=${auth}` : ""}`;
    fetch(apiUrl, {
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((data) => setUsers(data))
      .catch((error) => {
        setMessage(error.message);
        setVisible(true);
      });
  };

  useEffect(() => {
    fetchUsers(1, authParam);
    setCurrentPage(1);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [botonAdmin, botonPlayer]);

  useEffect(() => {
    fetchUsers(currentPage, authParam);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  function setname(nombreDeUsuario){setNombre(nombreDeUsuario);}

  function setboton(boton){
    if (boton===0){
      setBotonAll(true);
      setBotonAdmin(false);
      setBotonPlayer(false);
    }
    else if (boton===1){
      setBotonAll(false);
      setBotonAdmin(true);
      setBotonPlayer(false);
    }
    else{
      setBotonAll(false);
      setBotonAdmin(false);
      setBotonPlayer(true);
    }
  }

  const usersArray = users.content || [];

  const filterUsers = usersArray.filter((user) =>
    user.username.toLowerCase().includes(nombreBuscado.toLowerCase())
  );

  const userList = filterUsers.map((user) => {
    return (
      <tr key={user.id}>
        <td>{user.username}</td>
        <td>{user.authority.authority}</td>
        <td>
          <div className="options-row" style={{ gap: '0.5rem', margin: 0 }}>
            <Link
              to={"/users/" + user.id}
              className="auth-button blue"
              aria-label={"edit-" + user.id}
              style={{ padding: '0.5rem 1rem', fontSize: '0.8rem', minWidth: 'auto' }}
            >
              Edit
            </Link>
            <button
              className="auth-button danger"
              aria-label={"delete-" + user.id}
              onClick={() => {
                setToDelete({ id: user.id, username: user.username });
                setConfirmVisible(true);
              }}
              style={{ padding: '0.5rem 1rem', fontSize: '0.8rem', minWidth: 'auto' }}
            >
              Delete
            </button>
          </div>
        </td>
      </tr>
    );
  });
  const modal = getErrorModal(setVisible, visible, message);
  const confirmModal = (
    <ConfirmDeleteModal
      isVisible={confirmVisible}
      text={toDelete.username ? `¿Seguro que quieres borrar el usuario ${toDelete.username}?` : '¿Seguro que quieres borrar este usuario?'}
      onCancel={() => setConfirmVisible(false)}
      onConfirm={() => {
        deleteFromList(
          `/api/v1/users/${toDelete.id}`,
          toDelete.id,
          [filterUsers, () => fetchUsers(currentPage, authParam)],
          [alerts, setAlerts],
          setMessage,
          setVisible,
          { skipConfirm: true }
        );
        setConfirmVisible(false);
      }}
    />
  );

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Users</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      {confirmModal}
      <div className="custom-form-input" style={{ maxWidth: '400px', margin: '0 auto 1rem auto' }}>
        <input 
          type="search" 
          className="custom-input" 
          value={nombreBuscado} 
          onChange={(usuario) => setname(usuario.target.value)} 
          placeholder="Buscar usuario" 
        />
      </div>

      <div className="options-row">
        <button 
          id="0" 
          onClick={() => setboton(0)} 
          className={`auth-button ${botonAll ? "selected" : ""}`}
        > 
          All
        </button>
        <button 
          id="1" 
          onClick={() => setboton(1)} 
          className={`auth-button ${botonAdmin ? "selected" : ""}`}
        > 
          Admin
        </button>
        <button 
          id="2" 
          onClick={() => setboton(2)} 
          className={`auth-button ${botonPlayer ? "selected" : ""}`}
        > 
          Player
        </button>
      </div>
    
      <Link className="auth-button" style={{textDecoration: "none", marginBottom: "2rem"}} to="/users/new">
        Add User
      </Link>

      <div>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Username</th>
              <th>Authority</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </Table>
      </div>

      {users.totalPages > 1 && (
        <>
          <div style={{ textAlign: 'center', color: '#666' }}>
            {users.totalElements > 0
              ? `${(currentPage - 1) * 10 + 1}-${Math.min(currentPage * 10, users.totalElements)} de ${users.totalElements}`
              : 'No hay usuarios'}
          </div>
          <div className="options-row" style={{ justifyContent: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
            <button
              className="auth-button"
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
              disabled={currentPage === 1}
            >
              &lt;
            </button>

            {Array.from({ length: users.totalPages }, (_, i) => {
              const page = i + 1;
              const showPage = page === 1 || page === users.totalPages || (page >= currentPage - 1 && page <= currentPage + 1);

              if (!showPage && page === 2 && currentPage > 3) {
                return <span key="dots-start" style={{ color: '#676767', display: 'flex', alignItems: 'center', padding: '0 0.5rem' }}>...</span>;
              }

              if (!showPage && page === users.totalPages - 1 && currentPage < users.totalPages - 2) {
                return <span key="dots-end" style={{ color: '#676767', display: 'flex', alignItems: 'center', padding: '0 0.5rem' }}>...</span>;
              }

              if (!showPage) return null;

              return (
                <button
                  key={page}
                  className={`auth-button ${currentPage === page ? "selected" : ""}`}
                  onClick={() => setCurrentPage(page)}
                >
                  {page}
                </button>
              );
            })}

            <button
              className="auth-button"
              onClick={() => setCurrentPage(Math.min(users.totalPages, currentPage + 1))}
              disabled={currentPage === users.totalPages}
            >
              &gt;
            </button>
          </div>
        </>
      )}
    </div>
  );
}
