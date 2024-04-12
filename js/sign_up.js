const loginContainer = document.getElementById('login-container');
const signupContainer = document.getElementById('signup-container');
const switchToSignupBtn = document.getElementById('switch-to-signup');
const switchToLoginBtn = document.getElementById('switch-to-login');

/* 초기 상태 설정 */
loginContainer.style.display = 'none';
signupContainer.style.display = 'block';

/* login, signup 전환 */
switchToSignupBtn.addEventListener('click', () => {
    loginContainer.style.display = 'none';
    signupContainer.style.display = 'block';
});

switchToLoginBtn.addEventListener('click', () => {
    loginContainer.style.display = 'block';
    signupContainer.style.display = 'none';
});

/* login, signup 승인 */
