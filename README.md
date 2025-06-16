# Overview

This project is a backend solution for the Dragons of Mugloar game challenge. The goal of the application is to consistently score over 1000 points by making strategic decisions, solving tasks, buying items from the shop, and handling game mechanics via the Mugloar public API.

# Technologies Used

Java 17  
Maven  
Spring Boot 3.2.5  
JUnit 5 (Jupiter)  
REST communication (via Spring RestTemplate)  

# How to Run

Make sure you have Java 17 and Maven installed.

# Clone the repository
```bash
https://github.com/kpaloots/mugloar-dragon-trainer.git
```
# Navigate to the project directory
```bash
cd mugloar-dragon-trainer
```
# Build the project
```bash
mvn clean install
```
# Run the game loop
```bash
mvn spring-boot:run
```
The game will run in a loop until a score of 1000+ is reached.

# Unit Tests

The project includes unit tests for key logic components:

priorityValue() logic

reputationBonus() logic based on message type and reputation values

# To run tests:
```bash
mvn test
```
# Strategy

Task filtering: Tasks are filtered based on risk, message content, and current number of lives.

Dynamic risk management: The logic switches to a "safe mode" after multiple failures.

Reputation awareness: Tasks affecting people/state/underworld reputations are adjusted accordingly.

Live reputation feedback: Current reputation (People/State/Underworld) is printed after every task to monitor changes.

Shop strategy:

- Healing potions are prioritized when lives are low.  
- Upgrades (Claw Sharpening, Iron Plating, etc.) are purchased in a specific order.  
- A gold reserve is kept (default: 150) to avoid getting stuck without healing funds.  

# API Endpoints Used

/api/game/start - Start new game

/api/{gameId}/messages - Get list of tasks

/api/{gameId}/solve/{adId} - Solve a specific task

/api/{gameId}/shop - Get shop items

/api/{gameId}/buy/{itemId} - Buy an item

/api/{gameId}/investigate/reputation - Get reputation status

# Edge Case Handling

Prevents solving known trap tasks (e.g. those containing "super awesome diamond")