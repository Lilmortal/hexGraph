Working in progress...

The main goal of this project is to calculate all the hex code in a given image which will be provided by my raspberry pi taking a picture of the outside view every 3 seconds. The images will be push to a kafka topic, and the hexGraphConsumers will retrieve that image, split the workload of calculating the hex codes via the maximum number of threads available on your machine, and then display the results as well as a long term graph on a dashboard.
I will be working with Akka, using the actor model to handle my parallelism, as well as storing the results into Cassandra.

I might look into Apache Camel in terms of getting the images from elsewhere and copying it into a specfic folder (I haven't look too deeply into Camel yet so I don't know what can it do), as well as potentially using Apache Spark on working with the calculations.

In terms of the UI side, I was thinking of working with Vue, but I decided I am going to try and work with native JS and direct DOM manipulation again just for learning purposes, potentially looking into service workers for offline capability.

In terms of deployment, I am not skilled at the devops side, hence I am thinking of having a docker image for my zookeeper, 2 images for my 2 brokers, and potentially using Kubernetes to handle them for learning purposes too? I will also need to think about how to automate the deployment process to AWS EC2, S3 and Cloudfront, via Ansible and/or AWS Cloudformation.

I am hoping I can finish this project in 2 months max, HOPEFULLY! 


# Change kafka connect from standalone to distributed