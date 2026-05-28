describe('register / login and student CRUD', () => {

  it('Register new cypress user', () => {
    // GIVEN
    cy.intercept('POST', '/api/register', { statusCode: 200, body: {} }).as('register');
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
});
