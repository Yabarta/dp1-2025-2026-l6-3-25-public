import React, { useEffect } from "react";
import '../static/css/profile/profile.css';
import jwt_decode from "jwt-decode"; 

export default function ProfileScreen ({user}) {
    return (
        <div className="profileContainer">
            <div className="profile">
                <h1 className="title">Perfil</h1>
            </div>
            <div className="statistics">
                <h1 className="title">Estadísticas</h1>
            </div>
        </div>
        );
}