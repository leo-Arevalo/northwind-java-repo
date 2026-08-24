
/*
*cliente http que usa tokens y lo renueva si expira.
*/

import { authService } from "../services/authService.js";

export async function apiClient(url, options = {}) {
    let accessToken = authService.getAccessToken();


const fetchWithToken = async (token) => {
    return await fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    });
};

let response = await fetchWithToken(accessToken);

if(response.status === 401){
    try{
        const newAccessToken = await authService.refreshToken();
        response = await fetchWithToken(newAccessToken);
    }catch(err){
        console.warn("Invalid refresh token or expired, closing sesion.");
        authService.logout();
        throw err;
    }
}
return response;
}

/**
 * Helper para usar con .then() despues de apiClient(): valida que la
 * respuesta haya sido existosa ANTES de parsear el body como JSON.
 * 
 * Sin esto, un error 400/404/500 del backend ( que tambien devuelve un
 * body en formato JSON) se trataba como si hubiera sido exitoso, y el 
 * error quedaba enmascarado en vez de mostrarse
 */
export async function parseJsonOrThrow(response) {
    if(!response.ok){
        let message = `Error ${response.status}`;
        try{
            const body = await response.json();
            message = body.message || message;
        }catch(_){
            //el body no era JSON valido, quedamos en el mensaje generico
        }
        throw new Error(message);
    }
    return response.json();
}


