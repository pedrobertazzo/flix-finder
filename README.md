# FlixFinder

FlixFinder is a movie recommendation system powered by AI to help users discover films tailored to their preferences.
The application employs large language models to analyze user preferences and generate personalized movie recommendations across various genres.
It integrates with [TMDB](https://www.themoviedb.org/) to fetch more information about each LLM suggestion.

## ✨ Key Features
- **AI-Powered Recommendations**: Utilizes advanced language models to understand user preferences and generate contextually relevant movie suggestions
- **User Backlog**: Save recommended movies to a personal backlog for later viewing

## 🚀 Getting Started
### Prerequisites
- JDK 17 or later
- Gradle build tool
- API key for OpenAI
- API key for TMDB

### Configuration
Configure your application by modifying and renaming the `application-local.yaml.template` file:
``` yaml
# API keys and external service configuration
tmdb:
  api-key: your_tmdb_api_key
  base-url: https://api.themoviedb.org/3

# LLM configuration
langchain:
  api-key: your_llm_api_key
```
### Running the Application
``` bash
./gradlew bootRun
```
The application will be available at `http://localhost:8080`
## 🔍 API Endpoints
### Movie Recommendations
- `GET /api/movies/recommendations`: Get personalized movie recommendations
    - Query parameters:
        - `preferences`: User's movie preferences in natural language
        - `genres`: Optional genres to filter by

### User Backlog
- `GET /api/backlog/{userId}`: Get the user's saved movie backlog
- `POST /api/backlog/{userId}/add`: Add a movie to the user's backlog
- `DELETE /api/backlog/{userId}/item/{backlogItemId}`: Remove an item from the backlog

## 🧪 Testing
``` bash
./gradlew test
```