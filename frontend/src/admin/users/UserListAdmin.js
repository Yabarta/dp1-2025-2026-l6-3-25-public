import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [nombreBuscado , setNombre] = useState("");
  const [botonAll, setBotonAll] = useState(true);
  const [botonAdmin, setBotonAdmin] = useState(false);
  const [botonPlayer, setBotonPlayer] = useState(false);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

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

  const filter1Users = users.filter((user) =>
    user.username.toLowerCase().includes(nombreBuscado.toLowerCase())
  );

  const filterUsers = filter1Users.filter((user) =>
    (user.authority.authority.toLowerCase() === "admin" && botonAdmin) ||  (user.authority.authority.toLowerCase() === "player" && botonPlayer) || botonAll
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
              onClick={() =>
                deleteFromList(
                  `/api/v1/users/${user.id}`,
                  user.id,
                  [users, setUsers],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
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

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Users</h1>
      {alerts.map((a) => a.alert)}
      {modal}
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
    </div>
  );
}
