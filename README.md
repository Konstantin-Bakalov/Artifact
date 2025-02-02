# Setup

### Using Docker (recommended)
- Install docker if not already installed
- Navigate to `Dockerfile`
- Run `docker build . -t artifact`
- Optionally run `docker images` and make sure the image was created
- Run `docker run artifact`  

### Run locally
- Install `cs` by following the instructions on https://www.scala-lang.org/download
- Install `scala` by following the instructions on https://www.scala-lang.org/download/3.6.3.html
- Navigate to 	`Artifact.scala` and run `scala Artifact.scala`
- You should see some output on your console
