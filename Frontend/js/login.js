
import { authService } from "./services/authService.js";


document.getElementById('loginForm').addEventListener('submit', async(e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    try{
        await authService.login(username, password);
        window.location.href = 'customers.html';
    }catch(err){
        alert('Error de login: '+err.message);
    }
});
