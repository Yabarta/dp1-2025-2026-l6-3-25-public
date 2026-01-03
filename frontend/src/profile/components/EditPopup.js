import React from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';

export default function EditPopup({ showEditPopup, setShowEditPopup, validationSchema, playerData = {}, handleEditSubmit }) {
  if (!showEditPopup) return null;
  return (
    <div className="popupOverlay">
      <div className="popupContent">
        <h2 className="title">Editar Perfil</h2>
        <button onClick={() => setShowEditPopup(false)} className="closePopupButton">X</button>
        <Formik
          initialValues={{ nickname: playerData.nickname, email: playerData.email }}
          validationSchema={validationSchema}
          onSubmit={handleEditSubmit}
        >
          {({ isSubmitting }) => (
            <Form>
              <div className="formGroup">
                <label htmlFor="nickname">Nombre de usuario</label>
                <Field name="nickname" type="text" className="formControl" />
                <ErrorMessage name="nickname" component="div" className="error" />
              </div>
              <div className="formGroup">
                <label htmlFor="email">Email</label>
                <Field name="email" type="email" className="formControl" />
                <ErrorMessage name="email" component="div" className="error" />
              </div>
              <div className="formButtons">
                <button type="submit" className="editProfileButton" disabled={isSubmitting}>Guardar Cambios</button>
                <button type="button" className="watchHistoryButton" onClick={() => setShowEditPopup(false)}>Cancelar</button>
              </div>
            </Form>
          )}
        </Formik>
      </div>
    </div>
  );
}
