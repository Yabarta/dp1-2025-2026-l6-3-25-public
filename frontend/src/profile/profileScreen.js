import React, { useEffect, useRef, useState } from "react";
import '../static/css/profile/profile.css';
import jwt_decode from "jwt-decode"; 
import { useNavigate } from "react-router-dom";
//import { getUserDetail } from "../api/UserEndpoints";
//import * as yup from 'yup'
import { buildInitialValues } from "./Helper";

export default function ProfileScreen ({user}) {
    const navigate = useNavigate();
    const imageInputRef = useRef(null);
    const [userData, setUserData] = useState(jwt_decode(user));
    const [profilePic, setProfilePic] = useState(userData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");

    const [initialUserValues, setInitialUserValues] = useState({ name: null, email: null, profilePicture: null })
//   const validationSchema = yup.object().shape({
//     name: yup
//       .string()
//       .max(255, 'Name too long')
//       .required('Name is required'),
//     email: yup
//       .string()
//       .nullable()
//       .email('Please enter a valid email'),
//     profilePicture: yup
//       .string()
//       .nullable()
//       .url('Please enter a valid URL')
//   })
  
//     useEffect(() => {
//     async function fetchUserData () {
//       try {
//         const fetchedUser = await getUserDetail(userData.id)
//         setUserData(fetchedUser)
//         const initialValues = buildInitialValues(fetchedUser, initialUserValues)
//         setInitialUserValues(initialValues)
//       } catch (error) {
//         alert(`There was an error while retrieving user details (id ${userData.id}). ${error}`)
//         }
//     }
//     fetchUserData()
//   }, [userData, initialUserValues]);

    const handleChangeProfilePicture = () => {
        imageInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const image = event.target.files[0];
        if (image) {
            alert(`Archivo seleccionado: ${image.name}. Aún no está implementado xd.`);
            // Aquí se implementaría la lógica para subir la imagen al servidor y actualizar la foto de perfil del usuario
            // Seria de la siguiente manera(Obviamente llamando al backend con sus funciones correspondientes)
            //     const formData = new FormData();
            //     formData.append('profilePicture', image);
            //     try {
            //         const response = await updateUserProfilePicture(userData.id, formData);
            //         const updatedUser = await response.json();
            //         setProfilePic(updatedUser.profilePicture);
            //         alert('Imagen de perfil actualizada con éxito.');

            //     } catch (error) {
            //         console.error('Error:', error);
            //         alert(error.message);
            //     }
             }
    };
    // const updateUserProfile = async (values) => {
    // try {
    //     const updatedUser = await updateUser(values)
    //     setUserData(updatedUser)
    //     alert('Profile successfully updated');
    //     navigate('/profileScreen', { dirty: true })
    // } catch (error) {
    //   console.error('Error:', error);
    //   alert(error.message);
    // }
    // };

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
                        <img src={profilePic}
                            onClick={handleChangeProfilePicture}
                            alt="provisional"
                            className="profilePicture" />
                        <input
                            type="file"
                            ref={imageInputRef}
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                            accept="image/*"
                        />
                        <div style={{
                            display: "flex",
                            flexDirection: "column",
                            gap: "1rem",
                            width: "100%"
                        }}>
                        <div className="bg">
                            Nombre de usuario: {userData.username}
                        </div>
                        <div className="bg">
                            Correo electrónico: {userData.email}
                        </div>
                        <div className="bg">
                            Fecha de registro: {new Date(userData.createdAt).toLocaleDateString()}
                        </div>
                        </div>
                    </div>
                    <button className="editProfileButton" onClick={() => navigate('/editProfileScreen')}>Editar Perfil</button>
                    <div style={{ width: "100%" }}>
                        <h1 className="title">Estadísticas</h1>
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
                </div>
            </div>
        </div>
        );
}