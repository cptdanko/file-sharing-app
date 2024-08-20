describe('template spec', () => {
	before(() => {
		cy.visit("http://localhost:3000");
	});
	async function elementVisible(text) {
		const body = await cy.get('body');
		expect(body.find(text).length).to.eq(0);
	}
	it('Should check show files flow', async () => {
		await elementVisible('[data-test="hideFiles"]');
		cy.get("#email").type("kelnaca@gmail.com");
		cy.get("#password").type("temp");
		cy.get('[data-test="login"]').click();
		cy.get('[data-test="showFiles"]').click();
		cy.get('[data-test="hideFiles"]').then($button => {
			if($button.is(":visible")) {
				cy.log("As expected");
			}
		});
	})
});
