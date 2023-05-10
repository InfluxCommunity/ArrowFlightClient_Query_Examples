FROM debian:latest

# Install dependencies
RUN apt update \
    && apt install -y -V ca-certificates lsb-release wget g++ \
    && apt install -y -V ca-certificates lsb-release wget \
    && wget https://apache.jfrog.io/artifactory/arrow/$(lsb_release --id --short | tr 'A-Z' 'a-z')/apache-arrow-apt-source-latest-$(lsb_release --codename --short).deb \
    && apt install -y -V ./apache-arrow-apt-source-latest-$(lsb_release --codename --short).deb \
    && apt update \
    && apt install -y -V libarrow-dev \
    && apt install -y -V libarrow-glib-dev \
    && apt install -y -V libarrow-dataset-dev \
    && apt install -y -V libarrow-dataset-glib-dev \
    && apt install -y -V libarrow-flight-dev \
    && apt install -y -V libarrow-flight-glib-dev \
    && apt-get install -y -V  libgflags-dev \
    && apt-get install -y -V libarrow-flight-sql-dev 

# Copy the source code
COPY . /app
WORKDIR /app

# Set ARGs for --build-arg options passed in the build command
ARG DATABASE_NAME
ARG HOST
ARG TOKEN

# Set run-time ENVs from ARGs
ENV DATABASE_NAME=${DATABASE_NAME}
ENV HOST=${HOST}
ENV TOKEN=${TOKEN}

# Verify ENVs
# ARG HOST
# RUN echo "HOST=$HOST"
# RUN echo "TOKEN=$TOKEN"
# RUN echo "DATABSE_NAME=$DATABASE_NAME"

# Compile the source code
RUN g++ -o myprogram main.cc -larrow_flight -larrow -lgflags  -larrow_flight_sql -std=c++17

# Run the compiled program
CMD ["./myprogram"]

# I removed the following dependencies from the install instructions because I was getting 
# E: Unable to locate package libplasma-*
# && apt install -y -V libplasma-dev \
# && apt install -y -V libplasma-glib-dev \
# && apt install -y -V libgandiva-dev \
# && apt install -y -V libgandiva-glib-dev \
# && apt install -y -V libparquet-dev \
# && apt install -y -V libparquet-glib-dev \

