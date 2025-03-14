# Pokémon Web

## Tech Stack
- **Spring Boot**

## Implemented Features
- Integrated a downstream call to [https://fin-man.cloud/sbg-poke/api/pokedex](https://fin-man.cloud/sbg-poke/api/pokedex) for fetching a list of Pokémon, with optional pagination.
- Integrated a downstream call to [https://fin-man.cloud/sbg-poke/api/pokedex/{name}](https://fin-man.cloud/sbg-poke/api/pokedex/{name}) for retrieving detailed information about a specific Pokémon.
- Configured Swagger for API documentation at [https://fin-man.cloud/sbg-poke/api/swagger-ui/index.html](https://fin-man.cloud/sbg-poke/api/swagger-ui/index.html) (although there were misconfigurations, likely due to a setup issue on the server).

## Features Yet to be Implemented
- **Local Caching**: Initially planned to use Redis for caching, but opted for H2 due to potential deployment server limitations. However, this feature was not completed due to time constraints.

## CI/CD
- The application is running live at [https://fin-man.cloud/sbg-poke/api/pokedex](https://fin-man.cloud/sbg-poke/api/pokedex).
- Deployed on a Hostinger VPS using Github actions.

## Unfinished Tasks
- **Swagger UI**: There are some misconfigurations preventing proper access to the Swagger UI at [this link](https://fin-man.cloud/sbg-poke/api/swagger-ui/index.html).
- **Unit Testing**: Due to time management issues, I did not implement TDD and was unable to complete unit testing.
