<div align="center">
    <img src="https://i.ibb.co/1fBzgRN/Benchion-Sockets.png" alt="Logo" width="150">
</div>

<div align="center">

<h1 align="center" style="margin: 0;">Benchion Sockets Library</h1>
<p align="center" style="margin-top: 0; font-size: 1.2rem;">
    An netty based asynchronous socket library for benchion java applications
    <br />
    <a href="https://github.com/Benchion/sockets/wiki"><strong>📖 Documents 📖</strong></a>
    <br />
    <br />
    <a href="https://github.com/Benchion/sockets/issues">Report Bug</a>
    ·
    <a href="https://github.com/Benchion/sockets/issues">Request Feature</a>
  </p>
</div>
<div align="center">
    <a href="https://github.com/orgs/Benchion/people">
        <img src="https://img.shields.io/github/contributors/Benchion/sockets?style=for-the-badge"></img>
    </a>
    <a href="https://github.com/Benchion/sockets/network/members">
        <img src="https://img.shields.io/github/forks/Benchion/sockets?style=for-the-badge"></img>
    </a>
    <a href="https://github.com/Benchion/sockets/stargazers">
        <img src="https://img.shields.io/github/stars/Benchion/sockets?style=for-the-badge"></img>
    </a>
    <a href="https://github.com/Benchion/sockets/issues">
        <img src="https://img.shields.io/github/issues/Benchion/sockets?style=for-the-badge"></img>
    </a>
    <a href="https://github.com/Benchion/sockets/blob/main/LICENSE">
        <img src="https://img.shields.io/github/license/Benchion/sockets?style=for-the-badge"></img>
    </a>

</div>
<br/>
<br/>
<details>
  <summary style="font-size: 15px;">Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#contributors">Contributors</a></li>
  </ol>
</details>

<h2 style="margin: 0;">📗 About The Project</h2>
<p style="margin: 0; line-height: 0;">
Benchion Socket Library is an understandable asynchronous java library made to make your netty work easier.
</p>

Here's why:
* It is an **easy to use** library, no need to waste time anymore!
* It is an **understandable** library.
* It is an **asynchronous** library.

### Built With
<p style="margin: 0; line-height: 20px;">In this project, netty was used as socket library, gson was used as json library, lombok was used as code generation library.</p>

* [Netty.io](https://netty.io)
* [Gson](https://github.com/google/gson)
* [Lombok](https://projectlombok.org)

## 🌙 Getting Started

### ✨ Installation

1. Firstly clone this project with Git
```sh
   git clone https://github.com/Benchion/sockets.git
```
2. Read the documents
3. Code, code, code!

## ⚡ Usage
<h4 style="margin-top: 30px;">Creating Server</h4>

```java
  BenchionServer server = new BenchionServer(8888); // Creates a server
  server.build().run(); // Builds and runs the server
```

<br>
<h4 style="margin-top: 10px;">Creating Client & Connect a Server</h4>

```java
  BenchionClient client = new BenchionClient("hostname", 8888); // Creates a client
  client.build().connect(); // Builds and connects to server
```

For more examples, please refer to the [Documentation](https://gitbook.io)

## 🔐 License
Distributed under the MIT License. See `LICENSE` for more information.

## 📞 Contact
<a href="https://discordapp.com/users/309326498500968449"><img src="https://img.shields.io/badge/-Discord-black.svg?style=for-the-badge&logo=discord&logoColor=white&colorB=6366F1"></img></a>

## 🧑🏻‍💻 Contributors
<img src="https://i.ibb.co/cvBQ2Qj/Gimble-Logo-Design.png" width="100" style="border-radius: 15px"></img>
<img src="https://i.ibb.co/rHZn9SJ/pp-00000.png" width="100" style="border-radius: 15px"></img>
