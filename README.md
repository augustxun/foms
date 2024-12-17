## Introduction

FOMS (Food Order Management System) is a comprehensive solution designed to streamline the process of managing food orders in restaurants, catering businesses, or delivery services. It enables efficient order tracking, menu management, and customer service, helping businesses optimize operations and enhance customer satisfaction.

IDE: IntelliJ IDEA

Architecture: Maven

## Guide

### 1 SQL Data

1. Create a database called `reggie_db` in local environment
2. Then run `foms_dml.sql` and `foms_data.sql`

### 2 Configuration

<img src="https://i.imgur.com/tyin7W6.png" alt="image-20241216181946892" style="zoom:50%;" />

There are three places that need to be modified：

1. Use an available port like: 8083, then the application will run on 8083
2. Replace it with your own `username` and `password`
3. Replace the path with your own absolute path, like:/../image/

### 3 Start up

1. Load the Maven Dependencies
2. Run the Backend Application
3. Enter the link: localhost:8083/backend/index.html in your own browser