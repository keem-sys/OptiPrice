# OptiPrice

**OptiPrice** is an AI-powered price aggregator and comparison engine designed to 
help users find the best deals across major South African retailers (Shoprite, Checkers, and Pick n Pay).

By combining web scraping with local LLM-based semantic matching, 
OptiPrice groups identical products from different stores even 
when they have different names into a single product view.


## Key Features

*   **Multi-Store Scraping**: Automated data extraction from Shoprite, Checkers Sixty60, 
and Pick n Pay using Playwright.
*   **AI Deduplication (RAG)**: Uses local AI (Qwen 2.5 via Ollama) and Vector Search (`pgvector`) to identify 
* that "Clover Milk 2L" and "Clover Fresh Milk 2 Litre" are the same product.
*   **Automatic Categorization**: AI automatically classifies new products 
* into categories like Dairy, Bakery, Pantry, etc.
*   **Price History**: Tracks price changes over time to show historical trends for every item. 
*   **Modern Dashboard**: A fast, responsive frontend built with React, TypeScript, and Tailwind CSS.

---

## Tech Stack

### **Backend**
*   **Framework**: Spring Boot 4.x (Java 21)
*   **Scraping**: Playwright 
*   **AI Integration**: Spring AI
*   **Database**: PostgreSQL with `pgvector` extension
*   **ORM**: Spring Data JPA / Hibernate

### **AI Models (Local via Ollama)**
*   **Reasoning/Matching**: `Qwen 2.5:3b`
*   **Embeddings**: `qwen3-embedding:0.6b`

### **Frontend**
*   **Framework**: React 18+ (Vite)
*   **Language**: TypeScript
*   **Styling**: Tailwind CSS
*   **Icons**: Lucide React

---

## Project Structure

```text
OptiPrice-Project/
├── backend/             # Spring Boot Application
├── frontend/            # React + TypeScript (Vite)
├── docker-compose.yml   # Infrastructure (PostgreSQL + pgvector)
├── .env                 # Environment variables
└── README.md
```

---

## Getting Started

### 1. Prerequisites
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/)
*   [Ollama](https://ollama.com/)
*   Java 21+
*   Node.js & npm

### 2. Infrastructure
Start the database with the vector extension:
```bash
docker-compose up -d
```

### 3. AI Setup
Pull the required models in Ollama:
```bash
ollama pull qwen2.5
ollama pull qwen3-embedding:0.6b
```

### 4. Running the Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 5. Running the Frontend
```bash
cd frontend
npm install
npm run dev
```

---

## License
This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
*   Built as part of a technical portfolio project.
*   Special thanks to the open-source communities behind Playwright and Spring AI.
---