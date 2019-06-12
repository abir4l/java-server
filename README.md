# Java Server


This is a project to learn about how http servers actually work.
Http is a protocol, now that was confusing for me and in order to understand it better i began to start making a server in the language 
i use the most. 

Let understand the basics of what a HTTP request is. If you have ever made a get request from a telnet client, it is better understood there.
In simple terms HTTP means that a client will send you text in a format. For example below is a get request

> GET /index.html HTTP/1.1  
> Host: 0.0.0.0.0  
> \<additional headers\>

This here would be a get request so it must have the method name at the start of the request.
Any developer creating a http-client will always send a request like this
and if you're supposed to parse a http request you should parse a request only if it has the method at the start
and other conventions.

This would be a protocol. Both parties agree to follow it and are made based on the particular protocol.

Now this was a very confusing topic for me personally when i wanted to understand computer network and with making this
Java Server i was more clear on what the protocol actually mean.

With this i understood why it was HTTP(Hyper **Text Transfer** Protocol).
It's a text based protocol over TCP. 
Now i understood that i could make my own protocol, it was just
a convention setup for everyone. I could make my own version of HTTP but the browsers
couldn't make a request to my server.

Now the confusing part was TCP. As i read it was another network protocol but when i was working with HTTP
i just used a socket connection provided by Java. Based on my research
it's a protocol that works on the driver level and we only use the abstraction of sockets.
So maybe another repository regarding TCP some other day.


Looking over [Nano HTTPD](https://github.com/NanoHttpd/nanohttpd) helped me a lot during this project
i could finally read another project that was based on what i was working on and understand other developers
perspectives. I also found out what could be the improvement on this project more, because of the project.







