FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# Copy frontend static files (context is project root)
# Each folder goes to its correct location as expected by nginx.conf
COPY frontend/landing /usr/share/nginx/html/landing
COPY frontend/backoffice /usr/share/nginx/html/backoffice
COPY frontend/handheld /usr/share/nginx/html/handheld
COPY frontend/shared /usr/share/nginx/html/shared
COPY frontend/assets /usr/share/nginx/html/assets

# Expose port 80
EXPOSE 80

# Health check - use /actuator/health which goes through proxy
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/actuator/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
