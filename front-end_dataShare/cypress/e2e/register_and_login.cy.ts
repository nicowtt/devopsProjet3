describe('register and login', () => {

  const TOKEN = 'jwtToken';

  it('Register new cypress user', () => {
    // GIVEN
    cy.intercept('POST', '/api/users', { statusCode: 200, body: {} }).as('register');
    cy.visit('/register');

    // WHEN
    cy.get('input[formcontrolname="email"]').type('fonctionalUserRegister@gmail.com');
    cy.get('input[formcontrolname="confirmPassword"]').type('password');
    cy.get('input[formcontrolname="password"]').type('password');
    cy.get('[data-cy="register-btn"]').click();

    // THEN
    cy.wait('@register');
    cy.url().should('include', '/login');
  });

  it('Login with bad password', () => {
    // GIVEN
    cy.intercept('POST', '/api/users/login', { statusCode: 401, body: 'Unauthorized' }).as('loginFailed');
    cy.visit('/login');

    // WHEN
    cy.get('input[formcontrolname="email"]').type('fonctionalUserLogin@gmail.com');
    cy.get('input[formcontrolname="password"]').type('badPassword');
    cy.get('[data-cy="login-btn"]').click();

    // THEN
    cy.wait('@loginFailed');
    cy.get('.toast-error').should('contain', 'Email ou mot de passe incorrect.');
  });

  it('Login new cypress user', () => {
    // GIVEN
    cy.intercept('POST', '/api/users/login', { statusCode: 200, body: TOKEN }).as('login');
    cy.visit('/login');

    // WHEN
    cy.get('input[formcontrolname="email"]').type('fonctionalUserLogin@gmail.com');
    cy.get('input[formcontrolname="password"]').type('password');
    cy.get('[data-cy="login-btn"]').click();

    // THEN
    cy.wait('@login');
    cy.url().should('include', '/home');
  });
});
