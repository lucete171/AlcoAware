document.addEventListener('DOMContentLoaded', function() {
    const loginContainer = document.getElementById('login-container');
    const signupContainer = document.getElementById('signup-container');
    const switchToSignupBtn = document.getElementById('switch-to-signup');
    const switchToLoginBtn = document.getElementById('switch-to-login');
    //----------------------------------------------------------
    // 초기에 로그인 폼 보이기, 회원가입 폼 숨기기
    loginContainer.style.display = 'block';
    signupContainer.style.display = 'none';

    // Sign Up 버튼 클릭 시 이벤트 처리
    switchToSignupBtn.addEventListener('click', function(event) {
        event.preventDefault(); // 폼 제출 방지
        loginContainer.style.display = 'none';
        signupContainer.style.display = 'block';
    });

    // Login 버튼 클릭 시 이벤트 처리
    switchToLoginBtn.addEventListener('click', function(event) {
        event.preventDefault(); // 폼 제출 방지
        loginContainer.style.display = 'block';
        signupContainer.style.display = 'none';
    });

    //----------------------------------------------------------
    /* login, signup 승인 */
    const fs = require('fs');

    // Function to check if password and confirm password match
    function passwordsMatch(password, confirmPassword) {
        return password === confirmPassword;
    }

    // Function to check if the given ID already exists in user.json
    function isIdUnique(id) {
        const users = getUsers();
        return !users.some(user => user.id === id);
    }

    // Function to get users from user.json
    function getUsers() {
        try {
            const usersData = fs.readFileSync('user.json');
            return JSON.parse(usersData);
        } catch (error) {
            return [];
        }
    }

    // Function to add a new user to user.json
    function addUser(id, password) {
        const users = getUsers();
        users.push({ id, password });
        fs.writeFileSync('user.json', JSON.stringify(users, null, 2));
    }

    // Function to handle sign up process
    function signUp(id, password, confirmPassword) {
        if (!passwordsMatch(password, confirmPassword)) {
            console.log("Passwords do not match.");
            return;
        }

        if (!isIdUnique(id)) {
            console.log("This ID is already registered.");
            return;
        }

        addUser(id, password);
        console.log("Signed up successfully.");
    }

    // Function to handle login process
    function login(id, password) {
        const users = getUsers();
        const user = users.find(user => user.id === id);

        if (!user) {
            console.log("This ID is not registered.");
            return;
        }

        if (user.password !== password) {
            console.log("Incorrect password.");
            return;
        }

        console.log("Logged in successfully.");
        // move to index.html
        window.location.href = "index.html";
    }

    const signup_id = document.getElementById('signup-id');
    const signup_pw = document.getElementById('signup-pw');
    const confirm_pw = document.getElementById('confirm-pw');
    const login_id = document.getElementById('login-id');
    const login_pw = document.getElementById('login-pw');

    const login_btn = document.getElementById('Login');
    const signup_btn = document.getElementById('signUp');

    signup_btn.addEventListener('click', function(event) {
        event.preventDefault(); // 폼 제출 방지
        signUp(signup_id.value, signup_pw.value, confirm_pw.value);
    });
    
    login_btn.addEventListener('click', function(event) {
        console.log("Hi");
        event.preventDefault(); // 폼 제출 방지
        login(login_id.value, login_pw.value);
    });
    
});

