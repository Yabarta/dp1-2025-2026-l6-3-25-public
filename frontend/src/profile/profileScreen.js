import React, { useEffect } from "react";
import '../static/css/profile/profile.css';
import jwt_decode from "jwt-decode"; 

export default function ProfileScreen ({user}) {
    return (
        <div className="profileContainer">
            <div className="left">
                <div className="bg">
                    <h1 className="title">Perfil</h1>
                    <div style={{
                        display: "flex",
                        flexDirection: "row",
                        gap: "2rem"
                    }}>
                        <img src="https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png"
                            alt="provisional"
                            className="profilePicture" />
                        <div className="bg">
                        </div>
                    </div>
                </div>
                <div>
                    <div className="bg">
                        <h1 className="title">Partidas Recientes</h1>
                        <div style={{
                            display: "flex",
                            flexDirection: "column",
                            gap: "1rem",
                            width: "100%"
                        }}>
                            <div className="bg">
                            </div>
                            <div className="bg">
                            </div>
                            <div className="bg">
                            </div>
                        </div>

{/* De manera provisional, hasta que se pueda renderizar desde el backend mediante una llamada */}

                    </div>
                </div>
            </div>
            <div className="right">
                <div className="bg">
                    <h1 className="title">Estadísticas</h1>
                </div>
            </div>
            <div>

            </div>
        </div>
        );
}