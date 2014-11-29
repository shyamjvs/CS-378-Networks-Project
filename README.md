CS-378-Networks-Project
=======================
A prototype of a web proxy implementing many features like:
1. Caching (LRU/LFU)
2. HTTP Compression (Gzip)
3. Firewall (Blocked IP lists)
4. Adblocking (Uses Easylist)
5. Content filtering (Naive bayes classifier to filter adult content)
6. Support for HTTP GET/POST and HTTPS via tunnelling 
7. Prioritization of requests (+ prevents DDoS attack)
8. User Authentication (Using HTTP 407)
9. Logging
