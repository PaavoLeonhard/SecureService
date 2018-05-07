ssh -i /opt/keypairs/ditas-testbed-keypair.pem cloudsigma@31.171.247.162 << 'ENDSSH'
sudo docker rm -f ditas/SecureService || true
sudo docker pull ditas/SecureService:latest
sudo docker run -p 50008:8080 -d --name SecureService ditas/SecureService:latest
ENDSSH