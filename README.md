# To-Do List Application

## Overview

This is a To-Do List application built using Kotlin and Jetpack Compose. It provides functionalities to create, update, delete, and manage tasks with different priorities and tags. The application also includes features like notifications for task reminders, sorting and filtering tasks, and a responsive UI that adapts to different screen sizes.

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern:
- **Model**: Represents the data layer, which includes data classes and the database schema.
- **ViewModel**: Acts as a mediator between the View and the Model. It holds UI-related data that survives configuration changes.
- **View**: The UI layer, built using Jetpack Compose, that observes the ViewModel for data changes and displays the data.

### Key Components

- **Room Database**: Used for local data storage.
- **WorkManager**: Manages background tasks and scheduling notifications.
- **StateFlow**: Manages and observes UI state changes.

## Features

### Responsive Design

The UI is built using Jetpack Compose and is responsive to different screen sizes, ensuring a smooth user experience on both mobile and tablet devices.

### Database Operations

The app uses Room Database for storing tasks locally. It includes the following entities and DAO operations:
- `Task`: Data class representing a task.
- `TaskDao`: Data Access Object interface for CRUD operations on tasks.
- `TaskRepository`: Repository pattern for abstracting data operations.

### Notifications

The app includes push notifications for task reminders using WorkManager:
- `NotificationUtils`: Utility object for creating notification channels and showing notifications.
- `TaskReminderWorker`: Worker class that triggers the notification based on the task's due time.

### Task Management

The application allows users to:
- Create new tasks with titles, descriptions, due dates, tags, and priorities.
- Update existing tasks.
- Delete tasks with confirmation.
- Filter and sort tasks based on different criteria.

## Testing

The application includes unit tests for  Repository classes to ensure data operations and business logic are correct. It uses JUnit and Mockito for testing.
The application icludes UI tests for all components and basic interactions

---

# Aplikacja Lista Zadań

## Przegląd

To jest aplikacja Lista Zadań napisana w Kotlinie przy użyciu Jetpack Compose. Zapewnia funkcje tworzenia, aktualizowania, usuwania i zarządzania zadaniami o różnych priorytetach i tagach. Aplikacja obejmuje również funkcje takie jak powiadomienia o przypomnieniach o zadaniach, sortowanie i filtrowanie zadań oraz responsywny interfejs użytkownika, który dostosowuje się do różnych rozmiarów ekranu.

## Architektura

Aplikacja korzysta z wzorca architektonicznego MVVM (Model-View-ViewModel):
- **Model**: Reprezentuje warstwę danych, która obejmuje klasy danych i schemat bazy danych.
- **ViewModel**: Działa jako mediator między widokiem a modelem. Przechowuje dane związane z interfejsem użytkownika, które przetrwają zmiany konfiguracji.
- **View**: Warstwa interfejsu użytkownika, zbudowana przy użyciu Jetpack Compose, która obserwuje zmiany danych ViewModel i wyświetla dane.

### Kluczowe komponenty

- **Room Database**: Używana do lokalnego przechowywania danych.
- **WorkManager**: Zarządza zadaniami w tle i planowaniem powiadomień.
- **StateFlow**: Zarządza i obserwuje zmiany stanu interfejsu użytkownika.

## Funkcje

### Responsywny projekt

Interfejs użytkownika jest zbudowany przy użyciu Jetpack Compose i jest responsywny na różne rozmiary ekranu, zapewniając płynne wrażenia użytkownika zarówno na urządzeniach mobilnych, jak i tabletach.

### Operacje bazodanowe

Aplikacja korzysta z Room Database do przechowywania zadań lokalnie. Obejmuje następujące encje i operacje DAO:
- `Task`: Klasa danych reprezentująca zadanie.
- `TaskDao`: Interfejs obiektu dostępu do danych dla operacji CRUD na zadaniach.
- `TaskRepository`: Wzorzec repozytorium do abstrakcji operacji danych.

### Powiadomienia

Aplikacja obejmuje powiadomienia push dla przypomnień o zadaniach za pomocą WorkManager:
- `NotificationUtils`: Obiekt narzędziowy do tworzenia kanałów powiadomień i wyświetlania powiadomień.
- `TaskReminderWorker`: Klasa Worker, która wyzwala powiadomienie na podstawie terminu zadania.

### Zarządzanie zadaniami

Aplikacja umożliwia użytkownikom:
- Tworzenie nowych zadań z tytułami, opisami, terminami, tagami i priorytetami.
- Aktualizację istniejących zadań.
- Usuwanie zadań z potwierdzeniem.
- Filtrowanie i sortowanie zadań według różnych kryteriów.

## Testowanie

Aplikacja zawiera testy jednostkowe dla klas ViewModel i Repository, aby upewnić się, że operacje danych i logika biznesowa są poprawne. Używa JUnit i Mockito do testowania.
Aplikacja zawiera UI testy dla wszystkich komponentów
