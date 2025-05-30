# Klaravik Dev Test API

**Base URL**: `https://app.klaravik.dev/dev-test-api`  
**Version**: `1.0.0`

## 🔐 Security

**Authentication Type**: API Key  
**Header**: `X-API-Key`  
**Description**: The API key to authorize requests.

---

## 📂 Endpoints

### GET `/categories`

**Description**: Retrieve a list of categories.

**Security**: Requires API Key

**Responses**:
- `200 OK`: A JSON array of categories.
- `401 Unauthorized`: Invalid or missing API key.
- `404 Not Found`: Resource or file not found.
- `500 Internal Server Error`: Internal server error reading data file.

---

### GET `/products`

**Description**: Retrieve a list of products.

**Security**: Requires API Key

**Responses**:
- `200 OK`: A JSON array of products.
- `401 Unauthorized`: Invalid or missing API key.
- `404 Not Found`: Resource or file not found.
- `500 Internal Server Error`: Internal server error reading data file.

---

## 📦 Schemas

### 🔸 Category

| Property   | Type     | Required | Description       |
|------------|----------|----------|-------------------|
| id         | integer  | ✅        | Category ID       |
| headline   | string   | ✅        | Headline text     |
| level      | integer  | ✅        | Category level    |
| parentId   | integer  | ❌        | Parent category ID|

---

### 🔹 CategoryList

- Type: `array` of [Category](#-category)

---

### 🖼 MainImage

| Property       | Type   | Format | Required | Description     |
|----------------|--------|--------|----------|-----------------|
| imageUrlThumb  | string | uri    | ✅        | Thumbnail URL   |
| imageUrlLarge  | string | uri    | ✅        | Large image URL |

---

### 📦 Product

| Property             | Type     | Format     | Required | Description               |
|----------------------|----------|------------|----------|---------------------------|
| id                   | integer  |            | ✅        | Product ID                |
| name                 | string   |            | ✅        | Product name              |
| make                 | string   |            | ❌        | Manufacturer              |
| description          | string   |            | ❌        | Product description       |
| currentBid           | integer  |            | ✅        | Current highest bid       |
| endDate              | string   | date-time  | ✅        | Auction end date          |
| reservePriceStatus   | string   |            | ✅        | Reserve price status      |
| municipalityName     | string   |            | ✅        | Name of municipality      |
| mainImage            | object   |            | ❌        | [MainImage](#-mainimage)  |
| categoryLevel1       | integer  |            | ❌        | Top-level category        |
| categoryLevel2       | integer  |            | ❌        | Mid-level category        |
| categoryLevel3       | integer  |            | ❌        | Sub-category              |

---

### 📦 ProductList

- Type: `array` of [Product](#-product)

---

### ❗ Error

| Property | Type   | Description         |
|----------|--------|---------------------|
| error    | string | Error message text  |

---

## ⚠️ Standard Responses

- **401 Unauthorized**: Invalid or missing API key.
- **404 Not Found**: Resource or file not found.
- **500 Server Error**: Internal server error reading data file.
