
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
