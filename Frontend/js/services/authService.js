
/*
*
*Manejo de login, logout, refresh-token
*/

const AUTH_API = "http://localhost:8080/api";

export const authService = {

    //////////////////////////////////////////////////////

    async login(username, password) {
        const res = await fetch(`${AUTH_API}/login`, {
            method: "POST",
            headers: {"Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });
    
    if(!res.ok) throw new Error("Credenciales invalidas");

    const { accessToken, refreshToken } = await res.json();
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    return accessToken;

    },
////////////////////////////////////////////////////////////

    async logoutFromServer() {
        const refreshToken = this.getRefreshToken();

        try{
            await fetch("http://localhost:8080/api/logout", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ refreshToken })
            });
        }catch(err){
            console.warn("Error cerrando sesion en servidor:", err);
        }
        this.logout(); //limpia localStorage

    },
////////////////////////////////////////////////////////////



    getAccessToken() {
        return localStorage.getItem("accessToken");
    },

    getRefreshToken() {
        return localStorage.getItem("refreshToken");
    },
/////////////////////////////////////////////////////////////

async refreshToken(){
    const refreshToken = this.getRefreshToken();
    const res = await fetch(`${AUTH_API}/refresh`,{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({refreshToken})
    });
    if(!res.ok){
        this.logout();
        throw new Error("Refresh token invalid.");
    }
    const { accessToken } = await res.json();
    localStorage.setItem("accessToken", accessToken);
    return accessToken;
},

logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}
};





