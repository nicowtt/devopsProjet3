describe('Upload and download file', () => {

  const TOKEN = 'jwtToken';
  const fileUuid = 'C517ACAB-7E2D-4D99-9DDA-36D8D9FF95F3';

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

  it('Download a file when logged in', () => {
    // GIVEN
    const mockFile = {
      uuid: fileUuid,
      name: 'test-file.txt',
      size: 1_500_000,
      expiredAt: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
      hasPassword: false
    };

    cy.intercept('GET', `/api/files/${fileUuid}`, { statusCode: 200, body: 'file content' }).as('download');
    cy.intercept({ method: 'GET', url: `/api/files/${fileUuid}`, times: 1 }, mockFile).as('getFile');
    cy.window().then(win => win.localStorage.setItem('jwt_token', TOKEN));

    cy.visit(`/download/${fileUuid}`);
    cy.wait('@getFile');

    // WHEN
    cy.contains('test-file.txt').should('be.visible');
    cy.get('[data-cy="download-btn"]').should('not.be.disabled').click();

    // THEN
    cy.wait('@download');
  });
});
