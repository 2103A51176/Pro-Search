<h1 align="center">Pro-Search</h1>

**Pro-Search** is an Android application that allows users to efficiently search for keywords across multiple PDF, Word, and Excel documents. The app simplifies searching for content within various document formats by consolidating results in one place, saving time and effort.

## Project Overview

Pro-Search was inspired by the need to quickly find answers during open-book exams. In situations where internet access wasn't available, manually searching through each document for specific information was inefficient. Pro-Search enables users to select multiple documents in various formats and search through them simultaneously.

## Features

- Select and search across multiple PDFs, Word documents, and Excel files.
- Add or remove files dynamically before performing a search.
- Efficient keyword search with quick results display.
- Easy-to-use interface for selecting and managing files.

## Technologies Used

- **Java**: For the Android application logic and UI handling.
- **iText Library**: For reading and extracting text from PDF files.
- **Apache POI**: For handling Excel and Word documents.
- **Android RecyclerView**: For dynamic display of selected files.

## Setup and Installation

### Prerequisites

- Android Studio installed on your machine.
- Basic knowledge of Android development.

### Clone the Repository

```bash
git clone https://github.com/your-username/Pro-Search.git
cd Pro-Search

### Open in Android Studio
- Open Android Studio, click on "Open an existing project", and navigate to the folder where you cloned this project.
- Let Android Studio sync the project and install any required dependencies.

### Build and Run
- Once the project is open in Android Studio, connect an Android device or start an emulator.
- Click on the "Run" button to build and run the app.

## Usage
1. Launch the Pro-Search app.
2. Use the **Select PDFs** button to add one or more PDF, Word, or Excel files.
3. Click **Search** to input the keyword, and the app will search through all the selected files for the keyword.
4. To remove any selected file, use the **"X"** button next to the file name.

