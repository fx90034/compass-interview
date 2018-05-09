# Coding Interview Assignment

This assignment will be evaluated in two parts:

1. Does the code work and meet the requirements specified below?
2. It will provide a conversation piece for the in-person portion of interviews.

## Objectives

- Write a crawler that hits this endpoint and then proceeds to navigate to each link.
- The application should detect circular links to prevent an infinite loop.
- The application should use Java. Spring is acceptable too.
- There must be sufficient tests.
- Deployment will be by emailing a zip file or cloning a public git repo.


## Requirements

- Crawl all the links (below) and print out summary statistics at the end:
	- total number of http requests performed throughout the entire application
	- total number of successful requests
	- total number of failed requests (hint: think about what makes something successful or failed)
- You may use gradle, maven, docker, makefile, etc. --> use of gradle/maven wrappers is encouraged.

Starting Point: https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json

## What we are looking for?

- We want to see what you would produce as a working product. 
- We want to see how you think about all phases of developing software: building, testing, running, deploying
- Any amount of building, packaging, running should be as minimal as possible. 
- The overall goal is to run with minimal interaction. (i.e. one command is usually nice)
