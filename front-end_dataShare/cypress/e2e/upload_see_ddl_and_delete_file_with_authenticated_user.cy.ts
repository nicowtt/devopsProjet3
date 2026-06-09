describe('Upload and download file', () => {

  const TOKEN = 'jwtToken';
  const fileUuid = 'C517ACAB-7E2D-4D99-9DDA-36D8D9FF95F3';

  it('Upload a file when logged in', () => {
    // GIVEN
    cy.intercept('POST', '/api/files', { statusCode: 201, body: { uuid: fileUuid } }).as('upload');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));
    cy.visit('/upload');

    // WHEN
    cy.get('[data-cy="file-input"]').selectFile('cypress/fixtures/test-file.txt', { force: true });
    cy.get('[data-cy="upload-btn"]').click();

    // THEN
    cy.wait('@upload');
    cy.get('[data-cy="share-link"]').should('contain', fileUuid);
  });

  it('See uploaded file in space', () => {
    // GIVEN
    const mockFiles = [
      {
        uuid: fileUuid,
        name: 'test-file.txt',
        size: 1_500_000,
        createdAt: new Date().toISOString(),
        expiredAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
        hasPassword: false,
      }
    ];
    cy.intercept('GET', '/api/files', { statusCode: 200, body: mockFiles }).as('getFiles');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));

    // WHEN
    cy.visit('/space');
    cy.wait('@getFiles');

    // THEN
    cy.get('.file-name').should('contain', 'test-file.txt');
  });

  it('Download a file when logged in', () => {
    // GIVEN
    const mockFile = {
      uuid: fileUuid,
      name: 'test-file.txt',
      size: 1_500_000,
      expiredAt: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
      hasPassword: false
    };

    cy.intercept('GET', `/api/files/${fileUuid}`, mockFile).as('getFile');
    cy.intercept('POST', `/api/files/download/${fileUuid}`, { statusCode: 200, body: 'file content' }).as('download');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));

    cy.visit(`/download/${fileUuid}`);
    cy.wait('@getFile');

    // WHEN
    cy.contains('test-file.txt').should('be.visible');
    cy.get('[data-cy="download-btn"]').should('not.be.disabled').click();

    // THEN
    cy.wait('@download');
  });

  it('Delete a file from space', () => {
    // GIVEN
    const mockFiles = [
      {
        uuid: fileUuid,
        name: 'test-file.txt',
        size: 1_500_000,
        createdAt: new Date().toISOString(),
        expiredAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
        hasPassword: false,
      }
    ];
    cy.intercept('GET', '/api/files', { statusCode: 200, body: mockFiles }).as('getFiles');
    cy.intercept('DELETE', `/api/files/${fileUuid}`, { statusCode: 204 }).as('deleteFile');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));
    cy.visit('/space');
    cy.wait('@getFiles');

    // WHEN twice is for confirmation
    cy.get('.btn-delete').click();
    cy.get('.btn-delete').click();

    // THEN
    cy.wait('@deleteFile');
    cy.get('.file-name').should('not.exist');
  });

});
