describe('Upload file', () => {

  const TOKEN = 'jwtToken';

  it('Upload a file when logged in', () => {
    // GIVEN
    cy.intercept('POST', '/api/files', { statusCode: 201, body: { uuid: 'abc-123' } }).as('upload');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));
    cy.visit('/upload');

    // WHEN
    cy.get('[data-cy="file-input"]').selectFile('cypress/fixtures/test-file.txt', { force: true });
    cy.get('[data-cy="upload-btn"]').click();

    // THEN
    cy.wait('@upload');
    cy.get('[data-cy="share-link"]').should('contain', 'abc-123');
  });
});
