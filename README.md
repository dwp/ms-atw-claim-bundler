# ms-claim-bundler

A Java Springboot service within Access to Work (AtW) that will manage sending claim requests or contact information
updates to DRS.

Claim requests details (nino and claim reference) are put onto the "atw-send-claim" queue and contact information update
details (request id) are put onto the "atw-update-contact" queue.

Once the consumer has picked up a claim request / update contact information request from the queue, there are number of
steps that are carried out before sending the claim request to DRS:

- Retrieve the full claim request / updated contact information from ms-claim
- Send claim / contact information data to produce the PDF and get URI
- Add claim form / update contact information form to the List of Envelope Documents
- Create and add new payee form to List of Envelope Documents (this step is for claim requests)
- Add List of Envelope Documents to the Envelope
- Add the Envelope to the Batch Upload
- Publish the Batch Upload to DRS
- Update the claim requests / contact information status to "UPLOADED_TO_DOCUMENT_BATCH" in ms-claim


- Once a response has been returned from DRS, update the claim requests status to "AWAITING_AGENT_APPROVAL" in ms-claim.
  For updating contact information requests, update the status to "COMPLETED_DRS_UPLOAD" in ms-claim

## Development

To test this service, you will need to have the dependencies of this service running which are ms-claim and localstack.

Then to start the service, run:

```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Maintainer Team: Bluejay

Contributing file: ../CONTRIBUTING.md