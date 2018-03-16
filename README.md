# Data loader

## Description
Data loader ingests the following:
- Legal entities
- Service agreements
- Users
- Function groups
- Data groups
- Permissions
- Products
- Arrangements
- Transactions
- Contacts
- Payments
- Notifications
- Conversations

### Access control setup
- Root legal entity with user `admin` as entitlements admin
- Legal entities (under the root legal entity `C000000` - generated first time) per legal entity entry with users array in the files [legal-entities-with-users.json](src/main/resources/data/legal-entities-with-users.json) and [legal-entities-with-users-without-permissions.json](src/main/resources/data/legal-entities-with-users-without-permissions.json) - configurable, see section *Custom data*

For legal entities and users in the file [legal-entities-with-users.json](src/main/resources/data/legal-entities-with-users.json):
- Function groups for every business function with all privileges per legal entity from the input file
- Data group consisting of arrangements per legal entity from the input file
- All function groups and data groups are assigned to the users via master service agreement of the legal entities from the input file.

### Product summary setup
- Default products: [products.json](src/main/resources/data/products.json)
- Random arrangements (by default: between 10 and 30) per legal entities and users under: [legal-entities-with-users.json](src/main/resources/data/legal-entities-with-users.json)
- In case of current account arrangements random debit cards (by default: between 3 and 10) are associated

### Transactions setup
- By default ingesting transactions is disabled - configurable via property
- If enabled, random transactions (by default: between 10 and 50) per arrangement per today's date

### Service agreements setup
Default service agreements (each object represents one service agreement): [serviceagreements.json](src/main/resources/data/serviceagreements.json)
- Legal entity ids will be retrieved via the external user ids given in the json file to set up the service agreements.
- All function groups and data groups related to the legal entities of the consumers will be exposed to the service agreements.

### Users setup
By default only the following users are covered:
- Users with permissions as described under *Entitlements setup* and *Product summary setup*: [legal-entities-with-users.json](src/main/resources/data/legal-entities-with-users.json)
- Users without permissions under its own legal entity (no master service agreement, function and data groups associated): [legal-entities-with-users-without-permissions.json](src/main/resources/data/legal-entities-with-users-without-permissions.json)

If more/other users are required, you can provide your own `json` files, see *Custom data*.

### Extra data setup
- The following is available for ingestion, but by default disabled - configurable via property:
- Contacts with multiple accounts per user
- Payments per user
- Notifications on global target group
- Conversations per user

Note: This can be rerun on an existing environment which already contains data by setting the property `ingest.entitlements` to `false`

## How to run data loader
1. Provision an [Autoconfig](https://backbase.atlassian.net/wiki/x/94BtC) environment based on `dbs` or `dbs-microservices` stack with **at least** the following capabilities (based on default configuration:
```
capabilities="Entitlements,ProductSummary"
```
2. Run the data loader as follows:
```
java -Denvironment.name=your-env-00 -jar dataloader-jar-with-dependencies.jar
```
or, provided by default values, declared in the properties file.
For local environment:
```
java -Duse.local.configurations=false -jar dataloader-jar-with-dependencies.jar
```

### Note when running on environments with existing data
- No data will be removed from the environment
- It will check whether the following already exist, and if so, it will skip ingesting the existing item
    - Legal entities
    - Users
    - Products
- In case of existing users and master service agreements: it will try to re-use function groups based on business function
- When an existing function group is found, be aware that the privileges of the existing function group will remain in tact
- In case of custom service agreements, function groups are not re-used (new ones will be created)
- All other items will be added on top of the existing data
- However, if the external id of the existing root legal entity is not equal to `C000000`, this will fail the ingestion

## Custom configuration

The following properties can be set to custom values for different purposes: [environment.properties](src/main/resources/environment.properties)

Example:
```
java -Denvironment.name=your-env-00 -Darrangements.max=20 -Ddebit.cards.min=10 -Ddebit.cards.max=30 -Dtransactions-max=50 -jar dataloader-jar-with-dependencies.jar
```

## Custom data

### Run with custom data
```
java -Denvironment.name=your-env-00 -cp /path/to/custom/resources/folder/:dataloader-jar-with-dependencies.jar com.backbase.testing.dataloader.Runner
```
`/path/to/custom/resources/folder/` must contain the custom `json` files

### How to create custom data
Example for the `legal-entities-with-users.json` (other files are: `legal-entities-with-users-without-permissions.json`, `serviceagreements.json` and `products.json`):

1. Create json file named `legal-entities-with-users.json` with custom legal entities and assigned custom user list conforming existing format (in this case conforming: [legal-entities-with-users.json ](src/main/resources/data/legal-entities-with-users.json ))

By default if customizable fields have not been provided, system will generate randomized values for it.
Optional fields in the data structure:
- `legalEntityExternalId`
- `parentLegalEntityExternalId`
- `legalEntityName`
- `legalEntityType`

Mandatory fields:
- `userExternalIds` - array of Strings, representing User ids.

Each `userExternalIds` array consists of the users which will be ingested under the above legal entity.

Example:
```javascript
[
  {
    "legalEntityExternalId": "LE000002",
    "parentLegalEntityExternalId": "C000000",
    "legalEntityName": "Hong Kong Legal Entity",
    "legalEntityType": "CUSTOMER",
    "userExternalIds": [
      "U0091011",
      "U0091012",
      "U0091013",
      "U0091014",
      "U0091015"
    ]
  }
]
```
2. Place `legal-entities-with-users.json` in a folder named `data`

Note: it is also possible to name/place the json file differently with the specific properties: `legal.entities.with.users.json.location`, `legal.entities.with.users.without.permissions.json.location` and `serviceagreements.json.location`

## Questions and issues
If you have a question about the Data loader, or are experiencing a problem please contact the author, Kwo Ding via Hipchat or [e-mail](mailto:kwo@backbase.com)

## Contributing
You are welcome to provide bug fixes and new features in the form of pull requests. If you'd like to contribute, please be mindful of the following guidelines:

- All changes should be tested on an Autoconfig environment based on the latest versions.
- Please make one change per pull request.
- Use descriptive commit messages which will be used for release notes.
- Try to avoid reformats of files that change the indentation, tabs to spaces etc., as this makes reviewing diffs much more difficult.